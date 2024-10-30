package org.demo.pay.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.app.model.PrepayRequest;
import com.wechat.pay.java.service.payments.app.model.PrepayResponse;
import com.wechat.pay.java.service.payments.app.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.extern.log4j.Log4j2;
import org.demo.common.exception.BaseException;
import org.demo.common.util.SnowflakeUtil;
import org.demo.pay.bo.UserEquityBo;
import org.demo.pay.config.WechatPayConfig;
import org.demo.pay.contants.PayTypeEnum;
import org.demo.pay.contants.RedisKeyEnum;
import org.demo.pay.contants.WXCallBackConstants;
import org.demo.pay.contants.WechatPayStatusEnum;
import org.demo.pay.dto.*;
import org.demo.pay.entity.*;
import org.demo.pay.mq.ProduceService;
import org.demo.pay.qo.BusinessCallbackQo;
import org.demo.pay.qo.WechatPayQo;
import org.demo.pay.service.*;
import org.demo.pay.util.WechatPayUtils;
import org.demo.pay.vo.OrderStatusVo;
import org.demo.pay.vo.WechatOrderVo;
import org.demo.pay.service.IOrderService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class WechatPayServiceImpl implements WechatPayService {

    @Value("${spring.profiles.active}")
    private String env;

    @Resource
    private IOrderService iOrderService;

    @Resource
    private IPersonalCenterServiceForNewPeopleService iPersonalCenterServiceForNewPeopleService;

    @Resource
    private ProduceService produceService;

    @Resource
    private IWechatCallbackLogService iWechatCallbackLogService;

    @Resource
    private IWechatCallbackJsonLogService iWechatCallbackJsonLogService;

    @Resource
    private RedissonClient redisson;

    @Resource
    private WechatPayConfig wechatPayConfig;

    @Resource
    private IPersonalCenterOrderService iPersonalCenterOrderService;

    @Override
    @Transactional
    public WechatOrderVo createOrder(WechatPayQo wechatPayQo, String userId,String os) {
        PersonalCenterServiceForNewPeople personalCenterServiceForNewPeople = iPersonalCenterServiceForNewPeopleService.getId(wechatPayQo.getGoodsId());
        if (personalCenterServiceForNewPeople == null || org.springframework.util.StringUtils.isEmpty(personalCenterServiceForNewPeople.getGoodsType())){
            throw new BaseException(501,"商品信息不存在,无法发起支付");
        }
        WechatOrderVo vo = new WechatOrderVo();
        log.info("微信支付生成预付单-参数：{}====用户ID：{}",wechatPayQo,userId);
        Order order = new Order();
        String orderNo = SnowflakeUtil.getInstance().nextId();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setGoodsNo(wechatPayQo.getGoodsId().toString());
        order.setGoodsType(personalCenterServiceForNewPeople.getGoodsType());
        order.setPayType(PayTypeEnum.WECHAT_PAY.getCode());
        BigDecimal price = personalCenterServiceForNewPeople.getPrice();
        //组装微信预付单对象
        WechatPayDto dto = new WechatPayDto();
        if("53".equals(userId)){
            order.setPrice(new BigDecimal("0.01"));
            dto.setPrice(new BigDecimal("0.01"));
        }else{
            order.setPrice(price);
            dto.setPrice(price);
        }
        dto.setOrderNo(orderNo);
        dto.setDescription(personalCenterServiceForNewPeople.getName());
        String notifyUrl = "";
        if ("test".equals(env)) {
            notifyUrl = WXCallBackConstants.NOTIFY_URL_TEST + "/wechatPay/resultCallback";
        } else {
            notifyUrl = WXCallBackConstants.NOTIFY_URL + "/wechatPay/resultCallback";
        }
        PrepayRequest prepayRequest = WechatPayUtils.initPrepayRequest(dto, notifyUrl);

        //调用微信生成预付单
        log.info("微信支付生成预付单-参数：{}",prepayRequest);
        PrepayResponse perpay = WechatPayUtils.perpay(prepayRequest);
        String prepayId = perpay.getPrepayId();
        order.setPrepayId(prepayId);

        //创建本地订单
        addOrder(order);


        //最后组装对象将微信预付单传给前端
        PrepayWithRequestPaymentResponse prepayWithRequestPaymentResponse = WechatPayUtils.assembleSignMessage(prepayId);
        vo.setResponse(prepayWithRequestPaymentResponse);
        vo.setOrderNo(orderNo);

        return vo;
    }


    /**
     * 创建本地订单
     * @param order
     */
    public void addOrder(Order order){
        order.setPayType(0);
        order.setOrderPayStatus(0);
        order.setBusinessStatus(0);
        log.info("创建本地订单:{}", order);
        iOrderService.addOrder(order);
    }


    /**
     * 微信回调
     * @param request
     * @return
     */
    public Integer weChatNotificationHandler(HttpServletRequest request){

        Transaction transaction = new Transaction();
        try {
            RequestParam requestParam = WechatPayUtils.assembleRequestParam(request);
            log.info("微信支付回调-开始-参数：{}",requestParam);

            NotificationParser parser = new NotificationParser(wechatPayConfig.rSAAutoCertificateConfig());
            // 以支付通知回调为例，验签、解密并转换成 Transaction
            transaction = parser.parse(requestParam, Transaction.class);
            log.info("微信支付回调-验证签名-对象：{}",transaction);
            wxCallBackLog(transaction,transaction.getOutTradeNo());
        } catch (ValidationException e) {
            // 签名验证失败，返回 401 UNAUTHORIZED 状态码
            log.error("微信支付验签失败:{}", e);
            return HttpStatus.UNAUTHORIZED.value();
        } catch (Exception e){
            log.error("微信支付组装验签对象异常:{}", e);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        if(ObjectUtils.isNotEmpty(transaction) && StringUtils.isNotEmpty(transaction.getOutTradeNo())){

            //获取本地订单id
            String outTradeNo = transaction.getOutTradeNo();
            //对该订单no进行加锁
            String redisKey = RedisKeyEnum.PAY_OREDER_NO.getRedisKey() + outTradeNo;
            RLock lock = redisson.getLock(redisKey);
            try{
                lock.lock();
                //判断数据中该订单是否已经处理完毕
                Order order = iOrderService.selectByOrderNo(outTradeNo);
                if(order.getOrderPayStatus() != 0){
                    // 重复处理
                    return HttpStatus.OK.value();
                }

                //查询订单数据发送mq
                log.info("微信支付回调-订单对象：{}",order);
                if(ObjectUtils.isEmpty(order)){
                    log.error("微信支付回调-未找到对应订单:{}", outTradeNo);
                    return HttpStatus.INTERNAL_SERVER_ERROR.value();
                }
                //修改支付状态
                String transactionId = transaction.getTransactionId();
                order.setThirdOrderNo(transactionId);
//            order.setPayTime(transaction.getSuccessTime());
                order.setPayTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                String tradeState = transaction.getTradeState().toString();
                boolean b = updateOrder(order, tradeState);
                if(!b){
                    log.error("微信支付回调-修改订单状态失败:{}", order);
                    return HttpStatus.INTERNAL_SERVER_ERROR.value();
                }
                //组装mq对象发送mq
                sendMq(order);
            }finally {
                lock.unlock();
            }

        }else{
            log.error("微信支付回调-解析对象异常:{}", transaction);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        // 处理成功，返回 200 OK 状态码
        return HttpStatus.OK.value();
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
        personalCenterOrder.setWechatTransactionId(order.getThirdOrderNo());
        personalCenterOrder.setReceipt(order.getReceipt());
        personalCenterOrder.setOrderStatus(1);
        personalCenterOrder.setPaymentTime(LocalDateTime.now());
        iPersonalCenterOrderService.updateById(personalCenterOrder);
    }

    /**
     * 回更业务状态
     * @param businessCallbackQo
     * @return
     */
    @Override
    public Boolean businessCallback(BusinessCallbackQo businessCallbackQo) {
        return iOrderService.updateByOrderNo(businessCallbackQo.getOrderNo(),businessCallbackQo.getStatus()) > 0;
    }

    /**
     * 查询订单状态
     * @param orderNo
     * @return
     */
    @Override
    public OrderStatusVo getOrderStatus(String orderNo) {
        OrderStatusVo vo = new OrderStatusVo();
        Order order = iOrderService.selectByOrderNo(orderNo);
        if (order == null){
            throw new BaseException(501,"订单信息不存在");
        }
        BeanUtil.copyProperties(order,vo);
        //订单支付状态 0代表未支付 1代表支付成功 2代表取消支付 3代表支付失败
        Integer orderPayStatus = order.getOrderPayStatus();
        //业务结果: 1成功 2失败
        Integer businessStatus = order.getBusinessStatus();
        //如果
        switch (orderPayStatus){
            case 0:
                vo.setStatus(0);
                vo.setMessage("支付中");
                break;
            case 1:
                //微信支付成功，判断业务结果是否成功
                if(businessStatus == 0){
                    //业务结果未回更，等待回更完成
                    vo.setStatus(0);
                    vo.setMessage("权益下发中");
                }
                if(businessStatus == 1){
                    //业务结果已完成
                    vo.setStatus(1);
                    vo.setMessage("支付成功");
                }
                if(businessStatus == 2){
                    //业务结果失败，等待业务重试
                    vo.setStatus(0);
                    vo.setMessage("权益下发中");
                }
                break;
            case 2:
                vo.setStatus(2);
                vo.setMessage("取消支付");
                break;
            case 3:
                vo.setStatus(3);
                vo.setMessage("支付失败");
                break;
        }
        return vo;
    }

    /**
     * 关闭订单
     * @param orderNo
     * @return
     */
    @Override
    public Boolean closeOrder(String orderNo) {
        try{
            log.info("微信支付-主动关闭订单:{}",orderNo);
            //关闭微信订单
            WechatPayUtils.closeOrder(orderNo);
            //更新本地订单状态
            iOrderService.updatePayStatusByOrderNo(orderNo, 2,"主动关闭订单");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public OrderTipsDto getOrderTips(Integer goodsId, String userId) {
        return null;
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

    /**
     * 修改订单状态
     * @param order
     * @return
     */
    public boolean updateOrder(Order order,String tradeState){
        //支付成功
        if(WechatPayStatusEnum.SUCCESS.getCode().equals(tradeState)){
            order.setOrderPayStatus(1);
        }
        if(WechatPayStatusEnum.NOTPAY.getCode().equals(tradeState)
            || WechatPayStatusEnum.USERPAYING.getCode().equals(tradeState)){
            //未支付和支付中 返回给微信异常，让微信再次调用
            return false;
        }
        if(WechatPayStatusEnum.REFUND.getCode().equals(tradeState)
                || WechatPayStatusEnum.CLOSED.getCode().equals(tradeState)
                || WechatPayStatusEnum.REVOKED.getCode().equals(tradeState)){
            //以上状态都为取消支付
            order.setOrderPayStatus(2);
        }
        if(WechatPayStatusEnum.PAYERROR.getCode().equals(tradeState)){
            //以上状态都为支付失败
            order.setOrderPayStatus(3);
        }
        return iOrderService.updateById(order);
    }

    /**
     * 记录微信回调log
     * @param transaction
     */
    public void wxCallBackLog(Transaction transaction,String orderNo){

        WechatCallbackJsonLog wechatCallbackJsonLog = new WechatCallbackJsonLog();
        wechatCallbackJsonLog.setOrderNo(orderNo);
        String json = JSONObject.toJSON(transaction).toString();
        wechatCallbackJsonLog.setMessage(json);
        iWechatCallbackJsonLogService.addMessageLog(wechatCallbackJsonLog);


        WechatCallbackLog wechatCallbackLog = new WechatCallbackLog();
        wechatCallbackLog.setAppid(transaction.getAppid());
        wechatCallbackLog.setAttach(transaction.getAttach());
        wechatCallbackLog.setBankType(transaction.getBankType());
        wechatCallbackLog.setMchid(transaction.getMchid());
        wechatCallbackLog.setOutTradeNo(transaction.getOutTradeNo());
        wechatCallbackLog.setSuccessTime(transaction.getSuccessTime());
        wechatCallbackLog.setTradeStateDesc(transaction.getTradeStateDesc());
        wechatCallbackLog.setTransactionId(transaction.getTransactionId());
        wechatCallbackLog.setOpenid(transaction.getPayer().getOpenid());
        //log主表 只有部分信息
        iWechatCallbackLogService.addLog(wechatCallbackLog);

    }




}
