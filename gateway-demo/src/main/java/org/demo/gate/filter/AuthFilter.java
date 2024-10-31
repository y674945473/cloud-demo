package org.demo.gate.filter;

import lombok.extern.log4j.Log4j2;
import org.demo.common.exception.BaseException;
import org.demo.common.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static final String TOKEN_HEADER = "Authorization";

    private static List<String> auth_list = new ArrayList<>();

    static {
        auth_list.add("/user/register");
        auth_list.add("/user/refreshToken");
    }
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * auth确认，其中登录获取token的地址不进行验证
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("进入网关过滤器:AuthFilter");
        ServerHttpRequest request = exchange.getRequest();

        String requestPath = request.getPath().pathWithinApplication().value();
        if (auth_list.contains(requestPath)) {
            return chain.filter(exchange);
        }else{
            HttpHeaders headers = request.getHeaders();
            String authorization = headers.getFirst(TOKEN_HEADER);
            log.info("authorization: {}", authorization);
            boolean expired = JwtUtil.isExpired(authorization);
            //如果过期
            if(!expired){
                throw new BaseException(501,"token过期");
            }
            Map<String, Object> stringObjectMap = JwtUtil.extractInfo(authorization);
            if (stringObjectMap != null) {
                String uid = stringObjectMap.get("uid").toString();
                //将uid放入到redis中，保证其他服务直接获取
                log.info("uid: {}", uid);
            }else{
                throw new BaseException(502,"token无效");
            }
        }
        return chain.filter(exchange);
    }
}
