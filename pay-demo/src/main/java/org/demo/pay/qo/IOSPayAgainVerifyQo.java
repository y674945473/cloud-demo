package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IOSPayAgainVerifyQo {

    @ApiModelProperty("ios票据")
    @NotNull(message = "ios票据不能为空！")
    private String receipt;

    @ApiModelProperty("苹果商品")
    @NotNull(message = "苹果商品不能为空！")
    private String productId;

    @ApiModelProperty("苹果支付id")
    @NotNull(message = "苹果支付id不能为空！")
    private String transactionId;

    @ApiModelProperty("苹果支付时间")
    @NotNull(message = "苹果支付时间不能为空！")
    private String payTime;

    @ApiModelProperty("补偿类型：1前端触发，2运维人工干预")
    @NotNull(message = "补偿类型不能为空！")
    private String type;

}
