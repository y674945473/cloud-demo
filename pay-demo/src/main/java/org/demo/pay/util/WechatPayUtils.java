package org.demo.pay.util;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.cipher.Signer;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.core.util.IOUtil;
import com.wechat.pay.java.core.util.NonceUtil;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.app.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.demo.pay.config.WechatPayConfigProperties;
import org.demo.pay.dto.WechatPayDto;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

@Log4j2
public class WechatPayUtils {
    private static AppService appService = (AppService) SpringContextHolder.getBean("appService");

    private static Config config = (Config)SpringContextHolder.getBean("wechatConfig");

    private static WechatPayConfigProperties wechatPayConfigProperties =
            (WechatPayConfigProperties)SpringContextHolder.getBean("wechatPayConfigProperties");

    private static Signer signer = config.createSigner();


    public static PrepayResponse perpay(PrepayRequest request){
        return appService.prepay(request);
    }

    public static PrepayWithRequestPaymentResponse assembleSignMessage(String prepayId){
        long timestamp = Instant.now().getEpochSecond();
        String nonceStr = NonceUtil.createNonce(32);
        String message = wechatPayConfigProperties.appId + "\n" + timestamp + "\n" + nonceStr + "\n" + prepayId + "\n";
        log.debug("Message for RequestPayment signatures is[{}]", message);
        String sign = signer.sign(message).getSign();
        PrepayWithRequestPaymentResponse response = new PrepayWithRequestPaymentResponse();
        response.setAppid(wechatPayConfigProperties.appId);
        response.setPartnerId(wechatPayConfigProperties.merchantId);
        response.setPrepayId(prepayId);
        response.setPackageVal("Sign=WXPay");
        response.setNonceStr(nonceStr);
        response.setTimestamp(String.valueOf(timestamp));
        response.setSign(sign);
        return response;
    }

    /**
     * 组装验签对象
     * @param request
     * @return
     * @throws IOException
     */
    public static RequestParam assembleRequestParam(HttpServletRequest request) throws IOException {

        BufferedReader br = request.getReader();
        String str;
        StringBuilder body = new StringBuilder();
        while ((str = br.readLine())!=null) {
            body.append(str);
        }

        String wechatPaySerial = request.getHeader("Wechatpay-Serial");
        String wechatpayNonce = request.getHeader("Wechatpay-Nonce");
        String wechatTimestamp = request.getHeader("Wechatpay-Timestamp");
        String wechatSignature = request.getHeader("Wechatpay-Signature");
        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(String.valueOf(body))
                .build();


        return requestParam;

    }

    public static NotificationParser getNotificationParser() throws IOException {
        // 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
        // 没有的话，则构造一个
        String privateKey = IOUtil.toString(new ClassPathResource(wechatPayConfigProperties.privateKeyPath).getInputStream());
        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId(wechatPayConfigProperties.merchantId)
                .privateKey(privateKey)
//                .privateKeyFromPath()
                .merchantSerialNumber(wechatPayConfigProperties.merchantSerialNumber)
                .apiV3Key(wechatPayConfigProperties.apiV3key)
                .build();

        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(config);

        return parser;
    }

    /**
     * 关闭订单
     * @param orderNo
     */
    public static void closeOrder(String orderNo){
        CloseOrderRequest request = new CloseOrderRequest();
        request.setMchid(wechatPayConfigProperties.merchantId);
        request.setOutTradeNo(orderNo);
        appService.closeOrder(request);
    }


    /**
     *
     * @Title: appUnifiedPay
     * @Description: app支付
     * @param request
     * @return
     * @return PrepayWithRequestPaymentResponse    返回类型
     * @version V1.0
     */
    public static PrepayWithRequestPaymentResponse appUnifiedPay(PrepayRequest request) {
        log.info("config:{},signer:{},appService:{}--------", config, signer, config);
        String prepayId = appService.prepay(request).getPrepayId();
        long timestamp = Instant.now().getEpochSecond();
        String nonceStr = NonceUtil.createNonce(32);
        String message = request.getAppid() + "\n" + timestamp + "\n" + nonceStr + "\n" + prepayId + "\n";
        log.debug("Message for RequestPayment signatures is[{}]", message);
        String sign = signer.sign(message).getSign();
        PrepayWithRequestPaymentResponse response = new PrepayWithRequestPaymentResponse();
        response.setAppid(request.getAppid());
        response.setPartnerId(request.getMchid());
        response.setPrepayId(prepayId);
        response.setPackageVal("Sign=WXPay");
        response.setNonceStr(nonceStr);
        response.setTimestamp(String.valueOf(timestamp));
        response.setSign(sign);
        return response;

    }

    /**
     *
     * @param transactionId
     * @return
     */
    public static Transaction getTransactionById(String transactionId) {
        QueryOrderByIdRequest request = new QueryOrderByIdRequest();
        request.setTransactionId(transactionId);
        request.setMchid(wechatPayConfigProperties.merchantId);
        return appService.queryOrderById(request);
    }



    /**
     *
     * @Title: queryOrderById
     * @Description: APP查询订单
     * @param request
     * @return
     * @return Transaction    返回类型
     * @version V1.0
     */
    public static Transaction getAppOrderById(QueryOrderByIdRequest request) {
        return appService.queryOrderById(request);
    }

    /**
     *
     * @Title: closeAppOrder
     * @Description: 关闭APP订单
     * @param request
     * @return void    返回类型
     * @version V1.0
     */
    public static void closeAppOrder(CloseOrderRequest request) {
        appService.closeOrder(request);
    }

    /**
     *
     * @Title: initPrepayRequest
     * @Description:初始化商品支付信息
     * @param wechatPayDto
     * @return
     * @return PrepayRequest    返回类型
     * @version V1.0
     */
    public static PrepayRequest initPrepayRequest(WechatPayDto wechatPayDto, String notifyUrl) {
        PrepayRequest prepayRequest = new PrepayRequest();
        Amount amount = new Amount();
        BigDecimal price = wechatPayDto.getPrice();
        BigDecimal multiply = price.multiply(new BigDecimal(100));
        amount.setTotal(multiply.intValue());
        prepayRequest.setAmount(amount);
        prepayRequest.setOutTradeNo(wechatPayDto.getOrderNo());
        prepayRequest.setDescription(wechatPayDto.getDescription());
        prepayRequest.setAppid(wechatPayConfigProperties.appId);
        prepayRequest.setMchid(wechatPayConfigProperties.merchantId);
        prepayRequest.setNotifyUrl(notifyUrl);
        return prepayRequest;
    }


}
