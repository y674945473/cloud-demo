package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WechatPayQo {

    @ApiModelProperty("商品id")
    @NotNull(message = "商品id不能为空！")
    private Integer goodsId;

//    @ApiModelProperty("商品类型")
//    @NotBlank(message = "商品类型不能为空！")
//    private String goodsType;

    @ApiModelProperty("优惠券id")
    private Integer couponId;
}
