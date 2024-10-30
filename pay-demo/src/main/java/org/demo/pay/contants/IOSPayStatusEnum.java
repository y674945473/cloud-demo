package org.demo.pay.contants;

public enum IOSPayStatusEnum {
    RUNNING(0,"处理中"),
    SUCCESS(1,"处理完成"),
    FAIL(2,"处理失败"),
    REPEAT(3,"已处理，不能重复处理"),
    IOS_REPEAT(4,"ios订单重复支付"),
    IOS_NULL(5,"未找到订单"),
    IOS_OUT(6,"超过24小时");

    private final Integer code;
    private final String message;

    private IOSPayStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getWechatPayStatusEnum(Integer code){
        IOSPayStatusEnum[] values = IOSPayStatusEnum.values();
        for (IOSPayStatusEnum value :values) {
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
