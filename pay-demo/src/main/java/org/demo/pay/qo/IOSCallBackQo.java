package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IOSCallBackQo {

    @ApiModelProperty("回调加密参数")
    @NotNull(message = "回调加密参数不能为空！")
    private String signedPayload;
}
