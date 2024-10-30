package org.demo.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IOSStatusVo {


    @ApiModelProperty("支付状态")
    private Integer status;

    @ApiModelProperty("返回消息")
    private String message;
}
