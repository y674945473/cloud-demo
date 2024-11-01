package org.demo.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 */
@Getter
@Setter
@TableName("t_ios_verify_log")
public class IosVerifyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 购买商品数量
     */
    private String quantity;

    /**
     * 票据ID
     */
    private String transactionId;

    /**
     * 原始购买票据ID
     */
    private String originalTransactionId;

    /**
     * 购买时间
     */
    private String purchaseDate;

    /**
     * 购买时间戳
     */
    private String purchaseDateMs;

    /**
     * 购买时间（美国）no
     */
    private String purchaseDatePst;

    /**
     * 原始购买时间
     */
    private String originalPurchaseDate;

    /**
     * 原始购买时间戳
     */
    private String originalPurchaseDateMs;

    /**
     * 原始购买时间（美国）no
     */
    private String originalPurchaseDatePst;

    /**
     * 订阅到期时间
     */
    private String expiresDate;

    /**
     * 订阅到期时间戳
     */
    private String expiresDateMs;

    /**
     * 订阅到期时间（美国） no
     */
    private String expiresDatePst;

    /**
     * 是否在享受优惠价格期间
     */
    private String isInIntroOfferPeriod;

    /**
     * 是否享受免费试用
     */
    private String isTrialPeriod;

    /**
     * 跨设备购买事件（包括订阅更新事件）的唯一标识符。此值是识别订阅购买的主键
     */
    private String webOrderLineItemId;

    private String inAppOwnershipType;

    /**
     * 订单编号
     */
    private String orderNo;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;


    private Integer status;
}
