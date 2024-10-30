package org.demo.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户优惠券表
 * </p>
 *
 * @author hyk
 * @since 2024-05-15
 */
@Getter
@Setter
@TableName("t_discount_coupon_user")
public class DiscountCouponUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券名称
     */
    @ApiModelProperty("优惠券名称")
    private String name;

    /**
     * 优惠券id
     */
    @ApiModelProperty("优惠券id")
    private Long couponId;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long uid;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    /**
     * 类型 扣减类型1,额外赠送2
     */
    @ApiModelProperty("类型 扣减类型1,额外赠送2")
    private String couponType;

    /**
     * 起始金额
     */
    @ApiModelProperty("起始金额")
    private BigDecimal moneyLimit;

    /**
     * 设备限制 1安卓 2IOS 3小程序4鸿蒙 多个,分割
     */
    @ApiModelProperty("设备限制1安卓 2IOS 3鸿蒙4h5 多个,分割")
    private String deviceLimit;

    /**
     * 扣减金额
     */
    @ApiModelProperty("扣减金额")
    private BigDecimal discountAmount;

    /**
     * 额外赠送
     */
    @ApiModelProperty("额外赠送")
    private BigDecimal extraGift;

    /**
     * 活动类型
     */
    @ApiModelProperty("活动类型0大抽奖1手动发")
    private Integer activityType;

    /**
     * 活动类型
     */
    @ApiModelProperty("限制描述")
    private String icon;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String image;

    /**
     * 支付类型 0代表微信支付 1代表ios支付2边界贝支付3支付宝支付
     */
    @ApiModelProperty("支付类型")
    private String payType;

    /**
     * 商品id
     */
    @ApiModelProperty("商品id")
    private String goodsIds;

    /**
     * 订单id
     */
    @ApiModelProperty("订单id")
    private String orderNo;

    /**
     * 锁定时间
     */
    @ApiModelProperty("锁定时间")
    private String lockTime;

    /**
     * 状态 0待使用1锁定中2已使用
     */
    @ApiModelProperty("状态0待使用1锁定中2已使用")
    private String status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    /**
     * 限制
     */
    @ApiModelProperty("限制描述")
    private String limitDesc;

    /**
     * 用券时间类型0日期区间1长期2领券后生效
     */
    private Integer timeType;

    /**
     * 领券后有效期天数
     */
    private Integer timePeriod;

    /**
     * 活动id
     */
    private Integer activityId;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 活动主题
     * @return
     */
    private String activityTheme;

    @Override
    public String toString() {
        return "DiscountCouponUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", couponId=" + couponId +
                ", uid=" + uid +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", couponType='" + couponType + '\'' +
                ", moneyLimit=" + moneyLimit +
                ", deviceLimit='" + deviceLimit + '\'' +
                ", discountAmount=" + discountAmount +
                ", extraGift=" + extraGift +
                ", activityType=" + activityType +
                ", icon='" + icon + '\'' +
                ", image='" + image + '\'' +
                ", payType='" + payType + '\'' +
                ", goodsIds='" + goodsIds + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", lockTime='" + lockTime + '\'' +
                ", status='" + status + '\'' +
                ", createAt=" + createAt +
                ", limitDesc='" + limitDesc + '\'' +
                ", timeType=" + timeType +
                ", timePeriod=" + timePeriod +
                ", activityId=" + activityId +
                ", useTime=" + useTime +
                ", activityTheme=" + activityTheme +
                '}';
    }
}
