package org.demo.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderListVo {

    @ApiModelProperty("订单号")
    private String orderNumber;

    @ApiModelProperty("商品")
    private String title;

    @ApiModelProperty("价格")
    private String price;

    @ApiModelProperty("时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("赠送订单标识")
    private String typeMark;

    @ApiModelProperty("边界贝数量")
    private String cowryNum;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠券类型1扣减2额外赠送")
    private String couponType;

    @ApiModelProperty("优惠券扣减金额")
    private String couponDiscountAmount;

    @ApiModelProperty("优惠券额外赠送")
    private String couponExtraGift;

}
