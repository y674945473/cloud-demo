package org.demo.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;

/**
*
* 项目名称：bh_pro_base
* 类名称：ResultHandlerAdvice
* 类描述：统一响应体增强处理器
* @version V1.0 
*
*/
@SuppressWarnings("rawtypes")
@ControllerAdvice(basePackages = "org.bh.pro.**.controller")
public class ResultHandlerAdvice implements ResponseBodyAdvice {
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
        Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String) { // 如果Controller直接返回String的话，SpringBoot是直接返回，故我们需要手动转换成json。
            return objectMapper.writeValueAsString(Result.ok(body));
        }
        if (body instanceof Result) { // 如果返回的结果是ResultData对象，直接返回即可。
            return body;
        }
        return Result.ok(body);
    }
}
