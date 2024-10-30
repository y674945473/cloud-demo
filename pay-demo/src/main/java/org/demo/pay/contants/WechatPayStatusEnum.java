package org.demo.pay.contants;

public enum WechatPayStatusEnum {
    SUCCESS("SUCCESS","支付成功"),
    REFUND("REFUND","转入退款"),
    NOTPAY("NOTPAY","未支付"),
    CLOSED("CLOSED","已关闭"),
    REVOKED("REVOKED","已撤销（付款码支付）"),
    USERPAYING("USERPAYING","用户支付中（付款码支付）"),
    PAYERROR("PAYERROR","支付失败(其他原因，如银行返回失败)");

    private final String code;
    private final String message;

    private WechatPayStatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getWechatPayStatusEnum(String code){
        WechatPayStatusEnum[] values = WechatPayStatusEnum.values();
        for (WechatPayStatusEnum value :values) {
            if (value.getCode().equals(code)){
                return value.getMessage();
            }
        }
        return "";
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
