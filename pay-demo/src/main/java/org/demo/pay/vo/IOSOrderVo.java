package org.demo.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IOSOrderVo {

    private String orderNo;

    private String productId;

    @ApiModelProperty("提示语")
    private String tipsContent;

    @ApiModelProperty("是否能继续购买")
    private Boolean purchase_allowed = true;
}
