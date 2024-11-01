package org.demo.common.response;

/**
*
* 项目名称：bh_pro_base
* 类名称：ReturnCode
* 类描述：
* @version V1.0
*
*/
public enum ResultCodeEnum {
    RC200(1, "ok", "10000"), RC500(0, "fail", "20003"), RC10007(0, "登录信息失效", "10007"), RC10008(0, "参数有误", "10008");

    // 自定义状态码
    private final int status;

    // 自定义描述
    private final String info;

    // 自定义业务编码
    private final String infocode;

    ResultCodeEnum(int status, String info, String infocode) {
        this.status = status;
        this.info = info;
        this.infocode = infocode;
    }

    public int getStatus() {
        return status;
    }

    public String getInfo() {
        return info;
    }

    public String getInfocode() {
        return infocode;
    }

}
