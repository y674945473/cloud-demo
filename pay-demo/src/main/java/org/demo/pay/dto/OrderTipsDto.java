package org.demo.pay.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderTipsDto {

    @ApiModelProperty("提示语")
    private String tipsContent = "";

    @ApiModelProperty("是否能继续购买")
    private Boolean purchase_allowed = true;
}
