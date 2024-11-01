package org.demo.common.response;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.rpc.RpcException;
import org.demo.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.LoginException;

/**
*
* 项目名称：bh_pro_base
* 类名称：ExceptionHandlerAdvice
* 类描述：异常处理器增强器
* @version V1.0
*
*/
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> exception(BaseException e) {
        log.error("全局异常信息 BaseException ex={}", e.getMessage(), e);
        return Result.build(null, 0, e.getMessage(), e.getCode()+"");
    }

    @ExceptionHandler({RpcException.class})
    @ResponseStatus(HttpStatus.OK)
    public Result<String> RuntimeException(RpcException e) {
        log.error("全局异常信息 RuntimeException ex={}", e.getMessage(), e);
        return Result.build(null, 0, e.getMessage(), e.getErrorCode() +"");
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> RuntimeException(RuntimeException e) {
        log.error("全局异常信息 RuntimeException ex={}", e.getMessage(), e);
        if(e.getCause() != null && e.getCause() instanceof BaseException){
            return Result.build(null, 0, e.getCause().getMessage(), ((BaseException) e.getCause()).getCode()+"");
        }
        if (e.getMessage().contains("BaseException")) {
            return Result.build(null, 0, e.getMessage(), "20003");
        } else {
            return Result.fail();
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> exception(Exception e) {
        log.error("全局异常信息 ex={}", e.getMessage(), e);
        if (e instanceof LoginException) {
            return Result.loginFail();
        } else {
            return Result.fail();
        }
    }

}
