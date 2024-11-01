package org.demo.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 */
@Getter
@Setter
@TableName("t_wechat_callback_log")
public class WechatCallbackLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String appid;

    private String attach;

    private String bankType;

    private String mchid;

    /**
     * 订单No
     */
    private String outTradeNo;

    /**
     * 微信成功时间
     */
    private String successTime;
    /**
     * 交易状态，枚举值：
     * SUCCESS：支付成功
     * REFUND：转入退款
     * NOTPAY：未支付
     * CLOSED：已关闭
     * REVOKED：已撤销（付款码支付）
     * USERPAYING：用户支付中（付款码支付）
     * PAYERROR：支付失败(其他原因，如银行返回失败)
     */
    private String tradeState;

    private String tradeStateDesc;

    private String transactionId;

    private String openid;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private Integer deleted;
}
