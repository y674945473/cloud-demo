package org.demo.pay.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName: OrderCouponDto
 * @Description: TODO
 * @author: 55555
 * @date: 2024年05月15日 14:36
 */
@Data
public class OrderCouponParams implements Serializable {
    /**
     * 用户优惠券id
     */
    private Long couponUserId;

    /**
     * 金额
     */
    private BigDecimal price;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 设备
     */
    private String device;

    /**
     * 支付类型 0代表微信支付 1代表ios支付2边界贝支付3支付宝支付
     */
    private String payType;

    /**
     * 商品id
     */
    private String goodsId;

}
