package org.demo.pay.service.impl;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.log4j.Log4j2;
import org.demo.common.exception.BaseException;
import org.demo.common.util.SnowflakeUtil;
import org.demo.pay.contants.IOSPayStatusEnum;
import org.demo.pay.contants.PayTypeEnum;
import org.demo.pay.contants.RedisKeyEnum;
import org.demo.pay.dto.*;
import org.demo.pay.entity.*;
import org.demo.pay.mq.ProduceService;
import org.demo.pay.qo.IOSCallBackQo;
import org.demo.pay.qo.IOSPayAgainVerifyQo;
import org.demo.pay.qo.IOSPayVerifyQo;
import org.demo.pay.qo.WechatPayQo;
import org.demo.pay.service.*;
import org.demo.pay.util.IOSPayUtils;
import org.demo.pay.util.JwsUtil;
import org.demo.pay.vo.IOSOrderVo;
import org.demo.pay.vo.IOSStatusVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class IOSPayServiceImpl implements IOSPayService {

    @Resource
    private IPersonalCenterServiceForNewPeopleService iPersonalCenterServiceForNewPeopleService;

    @Resource
    private IOrderService iOrderService;

    @Resource
    private RedissonClient redisson;

    @Resource
    private IIosVerifyJsonLogService iosVerifyJsonLogService;

    @Resource
    private IIosVerifyLogService iosVerifyLogService;

    @Resource
    private ProduceService produceService;

    @Resource
    private IIosCallbackLogService iosCallbackLogService;

    @Resource
    private IIosCallbackVerifyLogService iosCallbackVerifyLogService;

    @Value("${applepay.verifyUrl}")
    private String url_apple_pay;

    @Value("${applepay.verifyTestUrl}")
    private String test_url_apple_pay;

    @Value("${applepay.shared.secret.password}")
    private String secretPassword;

    @Value("${applepay.shahe.type}")
    private String shaheType;

    @Resource
    private IPersonalCenterOrderService iPersonalCenterOrderService;

    @Transactional
    public IOSOrderVo createOrder(WechatPayQo wechatPayQo, String userId, String os) {
        PersonalCenterServiceForNewPeople personalCenterServiceForNewPeople = iPersonalCenterServiceForNewPeopleService.getId(wechatPayQo.getGoodsId());
        if (personalCenterServiceForNewPeople == null || org.springframework.util.StringUtils.isEmpty(personalCenterServiceForNewPeople.getGoodsType())){
            throw new BaseException(501,"商品信息不存在,无法发起支付");
        }
        log.info("IOS支付生成本地订单-参数：{}====用户ID：{}",wechatPayQo,userId);
        IOSOrderVo iosOrderVo = new IOSOrderVo();
        Order order = new Order();
        String orderNo = SnowflakeUtil.getInstance().nextId();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setGoodsNo(wechatPayQo.getGoodsId().toString());
        order.setGoodsType(personalCenterServiceForNewPeople.getGoodsType());
        order.setPayType(PayTypeEnum.IOS_PAY.getCode());
        //查询商品的信息：为了保证后续有可能有优惠券等
        if(ObjectUtils.isEmpty(personalCenterServiceForNewPeople) || personalCenterServiceForNewPeople.getPrice() == null){
            return iosOrderVo;
        }
        iosOrderVo.setProductId(personalCenterServiceForNewPeople.getIphoneProductId());
        BigDecimal price = personalCenterServiceForNewPeople.getPrice();
        order.setPrice(price);
        order.setProductId(personalCenterServiceForNewPeople.getIphoneProductId());
        //创建本地订单
        addOrder(order);

        iosOrderVo.setOrderNo(orderNo);
        return iosOrderVo;
    }

    @Override
    public IOSStatusVo iosVerify(IOSPayVerifyQo iosPayVerifyQo, String userId) {
        IOSStatusVo vo = new IOSStatusVo();
        // 票据
        String receipt = iosPayVerifyQo.getReceipt();
        // 服务端自己的订单号，可用做后续业务逻辑
        String orderNo = iosPayVerifyQo.getOrderNo();
        log.info("开始支付验签：{}==={}",orderNo,receipt);

        //对该订单no进行加锁
        String redisKey = RedisKeyEnum.PAY_OREDER_NO.getRedisKey() + orderNo;
        RLock lock = redisson.getLock(redisKey);
        try{
            lock.lock();
            //判断数据中该订单是否已经处理完毕
            Order order = iOrderService.selectByOrderNo(orderNo);
            if(order.getOrderPayStatus() != 0){
                log.error("ios支付 - 订单已处理完毕，订单号：{} - 票据：{}",orderNo,receipt);
                // 重复处理
                vo.setStatus(IOSPayStatusEnum.REPEAT.getCode());
                vo.setMessage(IOSPayStatusEnum.REPEAT.getMessage());
                return vo;
            }

            //将订单和票据绑定
            order.setReceipt(receipt);
            iOrderService.updateById(order);

            // 注意，有的票据在客户端接收时 加号 可能会被转换为 空格
            String data = receipt.replace(" ", "+");
            // 请求苹果服务器进行票据验证
            String result = IOSPayUtils.buyAppVerify(data,  iosPayVerifyQo.getPayType(),url_apple_pay,secretPassword);
            JSONObject receiptData = JSONObject.parseObject(result);
            // 解析票据
            if(result == null){
                // 解析票据失败 或 网络问题
                log.error("ios支付票据解析失败");
                order.setRemark("ios支付票据解析失败！");
                handleBusiness(order,3);
                vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                return vo;
            }else {

                // 支付环境是否正确
                int status = receiptData.getInteger("status");
                if(21007 == status){
                    log.info("ios支付 - 状态判断执行沙盒支付，订单号：{}",orderNo);
                    if("1".equals(shaheType)){
                        //判断是否关闭沙盒支付
                        log.error("ios支付-已关闭沙盒支付！订单No:{}",orderNo);
                        vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                        vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                        return vo;
                    }
                    // 验证失败21007 走沙箱环境
                    result = IOSPayUtils.buyAppVerify(data,  iosPayVerifyQo.getPayType(),test_url_apple_pay,secretPassword);
                    if(result == null){
                        //  解析票据失败
                        log.error("ios支付票据解析失败，状态码：21007");
                        order.setRemark("ios支付票据解析失败，状态码：21007！");
                        handleBusiness(order,3);
                        vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                        vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                        return vo;
                    }
                    receiptData = JSONObject.parseObject(result);
                    status = receiptData.getInteger("status");
                }
                //插入日志
                insertLog(receiptData,order);
                if(0 == status){

                    JSONObject receiptInfo = receiptData.getJSONObject("receipt");
                    JSONArray inAppList = receiptInfo.getJSONArray("in_app");
                    if(!CollectionUtils.isEmpty(inAppList)){
                        log.info("ios支付 - 验证成功，开始处理业务");
                        sortAppArray(inAppList);
                        JSONObject inApp = inAppList.getJSONObject(inAppList.size() - 1);
                        // 票据ID
                        String transactionId = inApp.getString("transaction_id");
                        //增加判断当前ios订单是否已经处理过
                        Order order1 = iOrderService.selectByTransactionId(transactionId);
                        if(!ObjectUtils.isEmpty(order1)){
                            log.error("ios支付-ios订单重复验证！订单No:{},票据ID:{}",orderNo,transactionId);
                            vo.setStatus(IOSPayStatusEnum.IOS_REPEAT.getCode());
                            vo.setMessage(IOSPayStatusEnum.IOS_REPEAT.getMessage());
                            return vo;
                        }
                        // 票据ID
                        String originalTransactionId = inApp.getString("original_transaction_id");
                        // 购买时间
                        String purchaseDate = inApp.getString("purchase_date");
                        int quantity = inApp.getInteger("quantity");
                        if(quantity > 1){
                            log.error("ios支付-商品数量不一致！");
                            order.setRemark("商品数量不一致！");
                            handleBusiness(order,3);
                            vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                            vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                            return vo;
                        }
                        // 商品ID 与在APP Store 后台配置的一致
                        String productId = inApp.getString("product_id");
                        String orderProductId = order.getProductId();
                        if(!productId.equals(orderProductId)){
                            log.error("ios支付-商品不一致！");
                            order.setRemark("商品不一致！");
                            handleBusiness(order,3);
                            vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                            vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                            return vo;
                        }

                        // 剩余业务逻辑
                        order.setThirdOrderNo(transactionId);
//                        order.setPayTime(purchaseDate);
                        order.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        order.setQuantity(1);
                        order.setOriginalTransactionId(originalTransactionId);
                        //修改订单状态，发送业务mq
                        handleBusiness(order,1);

                    }else{
                        // 获取in_app支付列表失败
                        log.error("ios支付列表数据异常");
                        order.setRemark("ios支付列表数据异常！");
                        handleBusiness(order,3);
                        vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                        vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                        return vo;
                    }
                }else{
                    // 获取in_app支付列表失败
                    log.error("ios支付状态：{}",status);
                    order.setRemark("ios支付状态："+status);
                    handleBusiness(order,3);
                    vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
                    vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
                    return vo;
                }

            }
            vo.setStatus(IOSPayStatusEnum.SUCCESS.getCode());
            vo.setMessage(IOSPayStatusEnum.SUCCESS.getMessage());
        }catch (Exception e){
            log.error("ios支付异常：",e);

            vo.setStatus(IOSPayStatusEnum.FAIL.getCode());
            vo.setMessage(IOSPayStatusEnum.FAIL.getMessage());
        }finally {
            //删除订单编号锁
            lock.unlock();
        }
        return vo;
    }


    public void sortAppArray(JSONArray inAppList){
        inAppList.sort(Comparator.comparing(v -> {
            String purchaseDate = ((JSONObject) v).getString("purchase_date");
            String substring = purchaseDate.substring(0, 19);
            return LocalDateTime.parse(substring, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }));
    }

    /**
     * 修改php订单状态
     * @param order
     */
    public void updatePhpOrder(Order order) {
        PersonalCenterOrder personalCenterOrder = iPersonalCenterOrderService.selectOrder(order);
        if(ObjectUtils.isEmpty(personalCenterOrder)){
            return;
        }
        personalCenterOrder.setAppleTransactionId(order.getOriginalTransactionId());
//        personalCenterOrder.setReceipt(order.getReceipt());
        personalCenterOrder.setOrderStatus(1);
        personalCenterOrder.setPaymentTime(LocalDateTime.now());
        iPersonalCenterOrderService.updateById(personalCenterOrder);
    }

    @Override
    public void callbackV2(IOSCallBackQo iosCallBackQo) {
        log.info("ios支付回调，参数：{}",iosCallBackQo);

        //首先插入log数据，解析前数据
        IosCallbackLog iosCallbackLog = new IosCallbackLog();
        iosCallbackLog.setSignedPayload(iosCallBackQo.getSignedPayload());
        iosCallbackLogService.save(iosCallbackLog);

        //将接收到的回调传入到mq，慢慢消费
        IosCallbackMqDto dto = new IosCallbackMqDto();
        dto.setSignedPayload(iosCallBackQo.getSignedPayload());
        dto.setCallbackId(iosCallbackLog.getId().toString());
        produceService.sendIosCallbackMQ(dto);
    }


    @Override
    public void verifyIosData(IosCallbackMqDto dto){
        log.info("ios回调---开始解析：{}",dto);
        IosCallbackLog iosCallbackLog = new IosCallbackLog();
        iosCallbackLog.setId(Long.parseLong(dto.getCallbackId()));
        IosCallbackVerifyLog iosCallbackVerifyLog = new IosCallbackVerifyLog();
        iosCallbackVerifyLog.setCallbackId(Long.parseLong(dto.getCallbackId()));
        //解析苹果请求的数据
        String signedPayload=new String(Base64.getDecoder().decode(dto.getSignedPayload().split("\\.")[0]));
        JSONObject jsonObject=JSONObject.parseObject(signedPayload);
        //解析数据
        Jws<Claims> result= JwsUtil.verifyJWT(jsonObject.getJSONArray("x5c").get(0).toString(),dto.getSignedPayload());

        //ios回调消息唯一标识
        String notificationUUID =result.getBody().get("notificationUUID").toString();
        //对回调进行加锁
        String redisKey = RedisKeyEnum.PAY_OREDER_NO.getRedisKey() + notificationUUID;
        RLock lock = redisson.getLock(redisKey);
        try{
            lock.lock();
            //判断数据中该订单是否已经处理完毕
            Long uuid = iosCallbackVerifyLogService.getUUID(notificationUUID);
            if(uuid > 0){
                log.error("ios回调 - 订单已处理，订单uuid：{}",notificationUUID);
                // 重复处理
                return;
            }
            //回调类型
            String notificationType=result.getBody().get("notificationType").toString();
            iosCallbackVerifyLog.setNotificationType(notificationType);

            Claims map=result.getBody();
            HashMap<String,Object> envmap=map.get("data",HashMap.class);
            String env=envmap.get("environment").toString();
            iosCallbackVerifyLog.setEnvironment(env);

            //解析回调具体信息
            String resulttran=new String(Base64.getDecoder().decode(envmap.get("signedTransactionInfo").toString().split("\\.")[0]));
            JSONObject jsonObjecttran=JSONObject.parseObject(resulttran);

            Jws<Claims> result3=JwsUtil.verifyJWT(jsonObjecttran.getJSONArray("x5c").get(0).toString(),envmap.get("signedTransactionInfo").toString());
            Claims body = result3.getBody();

            String transactionId=body.get("transactionId").toString();
            String originalTransactionId=body.get("originalTransactionId").toString();
            String webOrderLineItemId=body.get("webOrderLineItemId").toString();
            String bundleId=body.get("bundleId").toString();
            String productId=body.get("productId").toString();
            String subscriptionGroupIdentifier=body.get("subscriptionGroupIdentifier").toString();
            String purchaseDate=body.get("purchaseDate").toString();
            String originalPurchaseDate=body.get("originalPurchaseDate").toString();
            String expiresDate=body.get("expiresDate").toString();
            String quantity=body.get("quantity").toString();
            String type=body.get("type").toString();
            String inAppOwnershipType=body.get("inAppOwnershipType").toString();
            String signedDate=body.get("signedDate").toString();
            String transactionReason=body.get("transactionReason").toString();
            String storefrontId=body.get("storefrontId").toString();
            String price=body.get("price").toString();

            iosCallbackVerifyLog.setTransactionId(transactionId);
            iosCallbackVerifyLog.setOriginalTransactionId(originalTransactionId);
            iosCallbackVerifyLog.setWebOrderLineItemId(webOrderLineItemId);
            iosCallbackVerifyLog.setBundleId(bundleId);
            iosCallbackVerifyLog.setProductId(productId);
            iosCallbackVerifyLog.setSubscriptionGroupIdentifier(subscriptionGroupIdentifier);
            iosCallbackVerifyLog.setPurchaseDate(purchaseDate);
            iosCallbackVerifyLog.setOriginalPurchaseDate(originalPurchaseDate);
            iosCallbackVerifyLog.setExpiresDate(expiresDate);
            iosCallbackVerifyLog.setQuantity(quantity);
            iosCallbackVerifyLog.setType(type);
            iosCallbackVerifyLog.setInAppOwnershipType(inAppOwnershipType);
            iosCallbackVerifyLog.setSignedDate(signedDate);
            iosCallbackVerifyLog.setTransactionReason(transactionReason);
            iosCallbackVerifyLog.setStorefrontId(storefrontId);
            iosCallbackVerifyLog.setPrice(price);
            //通过历史票据id查询订单数据
            Order order = iOrderService.selectByOriginalTransactionId(originalTransactionId);
            if(ObjectUtils.isEmpty(order) || order.getId() == null){
                log.error("未查到本地订单数据，originalTransactionId：{}",originalTransactionId);
                iosCallbackLog.setStatus(1);
                iosCallbackLogService.updateById(iosCallbackLog);
                log.info("ios回调---解析结束：{}",dto);
                return;
            }

            /**
             * DID_RENEW
             * ORDINARYPURCHASE
             * PURCHASED
             * REFUND
             */

            switch (notificationType){
                case "CONSUMPTION_REQUEST":
                    //退款请求，提供消费数据
                    break;
                case "DID_CHANGE_RENEWAL_PREF":
                    //订阅计划进行了更改
                    break;
                case "DID_CHANGE_RENEWAL_STATUS":
                    //订阅计划进行了更改
                    break;
                case "DID_FAIL_TO_RENEW":
                    //续订失败，appstore将会在60天内重试扣费
                    break;
                case "DID_RENEW":
                    //成功订阅
                    //进行订阅处理
                    // 1、创建订阅订单，绑定之前的订阅信息 originalTransactionId

                    log.info("ios自动订阅-初始订单：{}",order);
                    String orderNo = SnowflakeUtil.getInstance().nextId();
                    Order orderNew = new Order();
                    orderNew.setOriginalTransactionId(order.getOriginalTransactionId());
                    orderNew.setUserId(order.getUserId());
                    orderNew.setProductId(productId);
                    orderNew.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                    orderNew.setPayTime(purchaseDate);
                    orderNew.setOrderNo(orderNo);
                    orderNew.setGoodsNo(order.getGoodsNo());
                    orderNew.setGoodsType(order.getGoodsType());
                    orderNew.setPayType(PayTypeEnum.IOS_PAY.getCode());
                    orderNew.setThirdOrderNo(transactionId);
                    orderNew.setBusinessStatus(0);
                    orderNew.setPrice(new BigDecimal(price).divide(new BigDecimal(1000)));
                    orderNew.setRemark("连续包月-续约");
                    orderNew.setQuantity(Integer.parseInt(quantity));
                    subscribe(orderNew);
                    updatePhpOrder(orderNew);
                    log.info("ios自动订阅-新订单：{}",orderNew);
                    //2、发送订阅mq出去，切割业务
                    PayMqDto payMqDto = new PayMqDto();
                    payMqDto.setGoodsId(order.getGoodsNo());
                    payMqDto.setOrderNo(orderNo);
                    payMqDto.setGoodsType(order.getGoodsType());
                    produceService.sendIosSubscribeMQ(payMqDto);
                    break;
                case "EXPIRED":
                    //订阅 过期
                    break;
                case "GRACE_PERIOD_EXPIRED":
                    //指示计费宽限期已结束而不续订订阅，因此你可以关闭对服务或内容的访问。
                    break;
                case "OFFER_REDEEMED":
                    //用户兑换了促销优惠或优惠代码
                    break;
                case "PRICE_INCREASE":
                    //系统已通知用户自动续订订阅价格上涨
                    break;
                case "REFUND":
                    //App Store 已成功退还可消费型 App 内购买项目、非消耗型 App 内购买项目、自动续期订阅或非续期订阅的交易。
                    //进行退款处理
                    log.info("ios退款-订单信息：{}",order);
                    //订单状态为支付成功
                    if (order.getOrderPayStatus() == 1) {
                        order.setOrderPayStatus(4);
                        //判断业务状态 如果是处理成功，向下处理退款业务；待处理和处理失败不进行业务处理
                        if(order.getBusinessStatus() == 1){
                            order.setBusinessStatus(3);
                            iOrderService.updateById(order);
                            //发送mq进行退款操作
                            //2、发送订阅mq出去，切割业务
                            PayMqDto refundDto = new PayMqDto();
                            refundDto.setGoodsId(order.getGoodsNo());
                            refundDto.setOrderNo(order.getOrderNo());
                            refundDto.setGoodsType(order.getGoodsType());
                            produceService.sendIosRefundMQ(refundDto);
                        }
                    }
                    break;
                case "REFUND_DECLINED":
                    //App Store 拒绝了 App 开发者使用以下任一方法发起的退款请求
                    break;
                case "REFUND_REVERSED":
                    //App Store 因客户提出的争议而撤销了之前授予的退款。
                    break;
                case "RENEWAL_EXTENDED":
                    //App Store 延长了特定订阅的订阅续订日期。
                    break;
                case "RENEWAL_EXTENSION":
                    // 正在尝试通过调用“延长所有活跃订阅者的订阅续订日期”来延长你请求的订阅续订日期。
                    break;
                case "REVOKE":
                    //用户有权通过“家人共享”进行的 App 内购买不再通过共享获得的通知类型。
                    break;
                case "SUBSCRIBED":
                    //用户订阅了产品。


                    break;
                case "TEST":
                    //测试
                    break;
            }
            iosCallbackLog.setStatus(1);
            //插入解析后的数据日志
            iosCallbackVerifyLogService.save(iosCallbackVerifyLog);
        }catch (Exception e){
            iosCallbackLog.setStatus(2);
            log.error("ios回调---处理异常！CallbackId:{}",dto.getCallbackId());
        }finally {
            lock.unlock();
        }
        iosCallbackLogService.updateById(iosCallbackLog);
        log.info("ios回调---解析结束：{}",dto);
    }

    /**
     * 订阅成功
     * @param order
     */
    public void subscribe(Order order){
        order.setOrderPayStatus(1);
        iOrderService.save(order);
    }

    /**
     * 创建本地订单
     * @param order
     */
    public void addOrder(Order order){
        order.setPayType(1);
        order.setOrderPayStatus(0);
        order.setBusinessStatus(0);
        log.info("ios支付 - 创建本地订单:{}", order);
        iOrderService.addOrder(order);
    }


    /**
     * 更新订单状态
     * @param order
     */
    public void handleBusiness(Order order,int status){
        //首先更新订单状态、订单信息
        order.setOrderPayStatus(status);
        iOrderService.updateById(order);
        //支付成功发送mq消息
        if(status == 1){
            //发送业务mq
            sendMq(order);
        }
    }


    /**
     * 发送mq消息 切割业务逻辑
     * @param order
     */
    public void sendMq(Order order){
        //组装mq对象发送mq
        PayMqDto dto = new PayMqDto();
        dto.setOrderNo(order.getOrderNo());
        dto.setGoodsType(order.getGoodsType());
        dto.setGoodsId(order.getGoodsNo());
        produceService.sendOrderMQ(dto);
    }

    @Override
    public IOSStatusVo iosAgainVerify(IOSPayAgainVerifyQo iOSPayAgainVerifyQo, String userId) {
        log.info("触发验签补偿：{}",iOSPayAgainVerifyQo.toString());
        String payTime = iOSPayAgainVerifyQo.getPayTime();
        LocalDateTime parse = LocalDateTimeUtil.parse(payTime.replace(" +0000",""),"yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDate =  parse.plusHours(8);
        LocalDateTime startDate = endDate.minusSeconds(300);
        //根据支付时间查询5分钟内的订单
        Order order = iOrderService.selectByProductId(userId, iOSPayAgainVerifyQo.getProductId(),startDate,endDate);
        if(order != null){
            log.info("触发验签补偿-订单No：{}",order.getOrderNo());
            if("1".equals(iOSPayAgainVerifyQo.getType())){
                LocalDateTime now = LocalDateTime.now();
                //计算当前时间和支付时间差
                long nowBetween = ChronoUnit.HOURS.between(now, endDate);
                if(nowBetween > 24){
                    log.info("触发验签补偿-超过24小时不处理-订单No：{}",order.getOrderNo());
                    IOSStatusVo vo = new IOSStatusVo();
                    vo.setStatus(IOSPayStatusEnum.IOS_OUT.getCode());
                    vo.setMessage(IOSPayStatusEnum.IOS_OUT.getMessage());
                    return vo;
                }
            }

            IOSPayVerifyQo qo = new IOSPayVerifyQo();
            qo.setReceipt(iOSPayAgainVerifyQo.getReceipt());
            qo.setPayType(0);
            qo.setTransactionId(iOSPayAgainVerifyQo.getTransactionId());
            qo.setOrderNo(order.getOrderNo());
            return iosVerify(qo,userId);
        }else{
            IOSStatusVo vo = new IOSStatusVo();
            vo.setStatus(IOSPayStatusEnum.IOS_NULL.getCode());
            vo.setMessage(IOSPayStatusEnum.IOS_NULL.getMessage());
            return vo;
        }
    }


    public void insertLog(JSONObject receiptData,Order order){
        IosVerifyJsonLog iosCallbackJsonLog = new IosVerifyJsonLog();
        iosCallbackJsonLog.setOrderNo(order.getOrderNo());
        iosCallbackJsonLog.setMessage(receiptData.toString());
        Integer status = receiptData.getInteger("status");
        iosCallbackJsonLog.setStatus(status.toString());
        iosVerifyJsonLogService.insertLog(iosCallbackJsonLog);
        if(status != 0){
            return;
        }

        JSONObject receiptInfo = receiptData.getJSONObject("receipt");
        JSONArray inAppList = receiptInfo.getJSONArray("in_app");
        IosVerifyLog iosCallbackLog = new IosVerifyLog();
        iosCallbackLog.setStatus(status);
        iosCallbackLog.setOrderNo(order.getOrderNo());
        JSONObject inApp = inAppList.getJSONObject(inAppList.size() - 1);
        String productId = inApp.getString("product_id");
        iosCallbackLog.setProductId(productId);
        String quantity = inApp.getString("quantity");
        iosCallbackLog.setQuantity(quantity);
        String transactionId = inApp.getString("transaction_id");
        iosCallbackLog.setTransactionId(transactionId);
        String originalTransactionId = inApp.getString("original_transaction_id");
        iosCallbackLog.setOriginalTransactionId(originalTransactionId);
        String purchaseDate = inApp.getString("purchase_date");
        iosCallbackLog.setPurchaseDate(purchaseDate);
        String purchaseDateMs = inApp.getString("purchase_date_ms");
        iosCallbackLog.setPurchaseDateMs(purchaseDateMs);
        String purchaseDatePst = inApp.getString("purchase_date_pst");
        iosCallbackLog.setPurchaseDatePst(purchaseDatePst);
        String originalPurchaseDate = inApp.getString("original_purchase_date");
        iosCallbackLog.setOriginalPurchaseDate(originalPurchaseDate);
        String originalPurchaseDateMs = inApp.getString("original_purchase_date_ms");
        iosCallbackLog.setOriginalPurchaseDateMs(originalPurchaseDateMs);
        String originalPurchaseDatePst = inApp.getString("original_purchase_date_pst");
        iosCallbackLog.setOriginalPurchaseDatePst(originalPurchaseDatePst);
        String expiresDate = inApp.getString("expires_date");
        iosCallbackLog.setExpiresDate(expiresDate);
        String expiresDateMs = inApp.getString("expires_date_ms");
        iosCallbackLog.setExpiresDateMs(expiresDateMs);
        String expiresDatePst = inApp.getString("expires_date_pst");
        iosCallbackLog.setExpiresDatePst(expiresDatePst);
        String isInIntroOfferPeriod = inApp.getString("is_in_intro_offer_period");
        iosCallbackLog.setIsInIntroOfferPeriod(isInIntroOfferPeriod);
        String isTrialPeriod = inApp.getString("is_trial_period");
        iosCallbackLog.setIsTrialPeriod(isTrialPeriod);
        String webOrderLineItemId = inApp.getString("web_order_line_item_id");
        iosCallbackLog.setWebOrderLineItemId(webOrderLineItemId);
        String inAppOwnershipType = inApp.getString("in_app_ownership_type");
        iosCallbackLog.setInAppOwnershipType(inAppOwnershipType);

        iosVerifyLogService.insertLog(iosCallbackLog);

    }





}
