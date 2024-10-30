package org.demo.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

/**
*
* 项目名称：bh_pro_base
* 类名称：ResultData
* 类描述：统一返回数据格式
* 创建人：xsq
* 创建时间：2023年6月24日 下午7:02:11
* 修改人：
* 修改时间：2023年6月24日 下午7:02:11
* 修改备注：
* @version V1.0 
*
*/
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    private int status;

    /**
     * 返回消息
     */
    private String info;

    /**
     * 业务编码
     */
    private String infocode;

    /**
     * 返回数据
     */
    private T data;

    public Result() {}

    // 返回数据
    @SuppressWarnings("unchecked")
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<T>();
        data = Optional.ofNullable(data).orElse((T)new Object());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> build(T body, int status, String info, String infocode) {
        Result<T> result = build(body);
        result.setStatus(status);
        result.setInfo(info);
        result.setInfocode(infocode);
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setStatus(resultCodeEnum.getStatus());
        result.setInfo(resultCodeEnum.getInfo());
        result.setInfocode(resultCodeEnum.getInfocode());
        return result;
    }

    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    /**
     * 操作成功
     * @param data  baseCategory1List
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.RC200);
    }

    public static <T> Result<T> fail() {
        return Result.fail(null);
    }

    public static <T> Result<T> paramFail() {
        return build(null, ResultCodeEnum.RC10008);
    }

    public static <T> Result<T> loginFail() {
        return build(null, ResultCodeEnum.RC10007);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.RC500);
    }

    public Result<T> info(String info) {
        this.setInfo(info);
        return this;
    }

    public Result<T> status(Integer status) {
        this.setStatus(status);
        return this;
    }
}
