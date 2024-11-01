package org.demo.pay.service;


import jakarta.servlet.http.HttpServletRequest;
import org.demo.pay.dto.OrderTipsDto;
import org.demo.pay.qo.BusinessCallbackQo;
import org.demo.pay.qo.WechatPayQo;
import org.demo.pay.vo.OrderStatusVo;
import org.demo.pay.vo.WechatOrderVo;


public interface WechatPayService {

    /**
     * 下单接口
     * @param wechatPayQo
     * @param userId
     * @return
     */
    WechatOrderVo createOrder(WechatPayQo wechatPayQo, String userId, String os);

    /**
     * 微信回调接口
     * @param request
     * @return
     */
    Integer weChatNotificationHandler(HttpServletRequest request);

    /**
     * 业务结果回调
     * @param businessCallbackQo
     * @return
     */
    Boolean businessCallback(BusinessCallbackQo businessCallbackQo);


    /**
     * 查询订单状态
     * @param orderNo
     * @return
     */
    OrderStatusVo getOrderStatus(String orderNo);

    /**
     * 关闭订单
     * @param orderNo
     * @return
     */
    Boolean closeOrder(String orderNo);


    OrderTipsDto getOrderTips(Integer goodsId, String userId);
}
