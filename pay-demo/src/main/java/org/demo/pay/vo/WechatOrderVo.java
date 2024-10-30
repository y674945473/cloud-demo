package org.demo.pay.vo;

import com.wechat.pay.java.service.payments.app.model.PrepayWithRequestPaymentResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WechatOrderVo {

    private PrepayWithRequestPaymentResponse response;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("提示语")
    private String tipsContent = "";

    @ApiModelProperty("是否能继续购买")
    private Boolean purchase_allowed = true;
}
