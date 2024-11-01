package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BusinessCallbackQo {

    @ApiModelProperty("订单编号")
    @NotBlank(message = "订单编号不能为空！")
    private String orderNo;

    @ApiModelProperty("业务状态")
    @NotNull(message = "业务状态不能为空！")
    private Integer status;
}
