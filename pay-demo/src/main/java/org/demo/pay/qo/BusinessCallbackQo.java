package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BusinessCallbackQo {

    @ApiModelProperty("订单编号")
    @NotBlank(message = "订单编号不能为空！")
    private String orderNo;

    @ApiModelProperty("业务状态")
    @NotNull(message = "业务状态不能为空！")
    private Integer status;
}
