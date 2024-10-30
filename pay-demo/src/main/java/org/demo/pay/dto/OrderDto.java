package org.demo.pay.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author yhx
 * @since 2024-02-02
 */
@Data
public class OrderDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商品编号
     */
    private String goodsNo;

    /**
     * 第三方订单编号,结合order_type查询
     */
    private String thirdOrderNo;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 订单类型 0代表普通订单 1权益消费订单
     */
    private Integer orderType;

    /**
     * 支付类型 0代表微信支付 1代表ios支付 2代表边界贝支付 3代表支付宝支付
     */
    private Integer payType;

    /**
     * 订单支付状态 0代表未支付 1代表支付成功 2代表取消支付 3代表支付失败 4代表退款
     */
    private Integer orderPayStatus;

    /**
     * 订单价格
     */
    private BigDecimal price;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 金额单位 0默认是元
     */
    private Integer unit;

    /**
     * 票据
     */
    private String receipt;

    /**
     * 内购产品ID 暂时只有IOS内购需要申请product_id
     */
    private String productId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    /**
     * 逻辑删除字段 0逻辑未删除  1逻辑删除
     */
    private Integer deleted;


    /**
     * 商品类型
     */
    private String goodsType;

    /**
     * 业务结果 0待处理、1处理成功、2处理失败、3退款处理中、4退款处理成功、5退款处理失败
     *
     */
    private Integer businessStatus;

    /**
     * 微信预付标识
     */
    private String prepayId;

    /**
     * 支付时间
     */
    private String payTime;

    private Integer quantity;


    private String originalTransactionId;

    /**
     * 边界贝数量
     */
    private BigDecimal cowryNum;

    /**
     * 用户优惠券id
     */
    private Integer couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券类型1扣减2额外赠送
     */
    private Integer couponType;

    /**
     * 优惠券扣减金额
     */
    private BigDecimal couponDiscountAmount;

    /**
     * 优惠券额外赠送
     */
    private BigDecimal couponExtraGift;

}
