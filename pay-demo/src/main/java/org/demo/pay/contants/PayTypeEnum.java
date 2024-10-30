package org.demo.pay.contants;

public enum PayTypeEnum {
    WECHAT_PAY(0,"微信支付"),
    IOS_PAY(1,"ios支付"),
    COWRY_PAY(2,"边界贝支付"),
    ALIPAY_PAY(3,"支付宝支付")

    ;

    private final Integer code;
    private final String message;

    private PayTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getWechatPayStatusEnum(Integer code){
        PayTypeEnum[] values = PayTypeEnum.values();
        for (PayTypeEnum value :values) {
            if (value.getCode().equals(code)){
                return value.getMessage();
            }
        }
        return "";
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
