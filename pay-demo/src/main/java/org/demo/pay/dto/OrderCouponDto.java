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
public class OrderCouponDto implements Serializable {

    /**
     * 用户优惠券id
     */
    private Long couponUserId;

    /**
     * 类型 扣减类型1,额外赠送2
     */
    private String couponType;

    /**
     * 扣减金额
     */
    private BigDecimal discountAmount;

    /**
     * 额外赠送
     */
    private BigDecimal extraGift;

    /**
     * 校验
     */
    private Boolean verify;

    /**
     * 失败原因
     */
    private String fail;

    /**
     * 优惠券名称
     */
    private String name;
}
