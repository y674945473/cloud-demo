package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IOSPayVerifyQo {

    @ApiModelProperty("订单no")
    @NotNull(message = "订单no不能为空！")
    private String orderNo;

    @ApiModelProperty("ios票据")
    @NotNull(message = "ios票据不能为空！")
    private String receipt;

    @ApiModelProperty("支付类型：0正常支付；1连续包月")
    @NotNull(message = "支付类型不能为空！")
    private Integer payType;

    @ApiModelProperty("苹果支付id")
    @NotNull(message = "苹果支付id不能为空！")
    private String transactionId;

}
