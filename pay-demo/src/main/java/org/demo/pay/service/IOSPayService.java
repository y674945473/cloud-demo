package org.demo.pay.service;


import org.demo.pay.dto.IosCallbackMqDto;
import org.demo.pay.entity.Order;
import org.demo.pay.qo.IOSCallBackQo;
import org.demo.pay.qo.IOSPayAgainVerifyQo;
import org.demo.pay.qo.IOSPayVerifyQo;
import org.demo.pay.qo.WechatPayQo;
import org.demo.pay.vo.IOSOrderVo;
import org.demo.pay.vo.IOSStatusVo;

public interface IOSPayService {


    /**
     * ios创建订单
     * @param wechatPayQo
     * @param userId
     * @return
     */
    IOSOrderVo createOrder(WechatPayQo wechatPayQo, String userId, String os);


    /**
     * ios验证
     * @param iosPayVerifyQo
     * @param userId
     * @return
     */
    IOSStatusVo iosVerify(IOSPayVerifyQo iosPayVerifyQo, String userId);


    /**
     * ios回调数据保存，发送mq
     * @param iosCallBackQo
     * @return
     */
    void callbackV2(IOSCallBackQo iosCallBackQo);

    /**
     * 解析ios回调参数
     * @param dto
     */
    void verifyIosData(IosCallbackMqDto dto);

    void sendMq(Order order);


    /**
     * ios验证
     * @param iOSPayAgainVerifyQo
     * @param userId
     * @return
     */
    IOSStatusVo iosAgainVerify(IOSPayAgainVerifyQo iOSPayAgainVerifyQo, String userId);
}
