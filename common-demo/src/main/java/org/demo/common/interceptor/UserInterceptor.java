package org.demo.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.demo.common.dto.UserDto;
import org.demo.common.exception.BaseException;
import org.demo.common.util.JwtUtil;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Log4j2
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String servletPath = request.getServletPath();
        String authorization = request.getHeader("Authorization");
        Map<String, Object> stringObjectMap = JwtUtil.extractInfo(authorization);
        if (stringObjectMap != null) {
            String uid = stringObjectMap.get("uid").toString();
            log.info("解析token获得uid: {}", uid);
            UserDto user = new UserDto();
            user.setId(Long.parseLong(uid));
            user.setName(stringObjectMap.get("name").toString());
            UserHelper.setUser(user);
            return true;
        }else{
            throw new BaseException(502,"token无效");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        // 清空
        UserHelper.remove();
    }
}
