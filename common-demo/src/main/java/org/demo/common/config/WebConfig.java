package org.demo.common.config;

import com.google.common.collect.Lists;
import org.demo.common.interceptor.UserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 * @Author: uranus
 * @Date: 2023-11-19 10:38
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final List<String> NO_AUTH_LIST = Lists.newArrayList();

    static {
        NO_AUTH_LIST.add("/user/register");
        NO_AUTH_LIST.add("/user/refreshToken");
    }


    @Bean
    public UserInterceptor userInterceptor() {
        return new UserInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api-docs","/api-docs/**","/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/doc/swagger-ui.html")
                .excludePathPatterns(
                        NO_AUTH_LIST
                );
    }
}

