package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AliPayQo {


    @ApiModelProperty("商品id")
    @NotNull(message = "商品id不能为空！")
    private Integer goodsId;

    @ApiModelProperty("优惠券id")
    private Integer couponId;

}
