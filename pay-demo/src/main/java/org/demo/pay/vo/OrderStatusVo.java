package org.demo.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderStatusVo {

    @ApiModelProperty("支付状态" +
            "0：支付中，等待1秒继续查询，最多查询3次\n" +
            "1：支付成功\n" +
            "2：取消支付\n" +
            "3：支付失败")
    private Integer status;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("订单价格")
    private BigDecimal price;

    @ApiModelProperty("备注说明")
    private String remark;

    @ApiModelProperty("支付类型 0代表微信支付 1代表ios支付 2代表边界贝支付 3代表支付宝支付")
    private Integer payType;

    @ApiModelProperty("支付时间")
    private String payTime;

    @ApiModelProperty("边界贝数量")
    private BigDecimal cowryNum;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠券额外赠送")
    private BigDecimal couponExtraGift;


}
