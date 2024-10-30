package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CouponMsgQo {

    @ApiModelProperty("类型0支付1生成报告")
    private Integer type;

}
