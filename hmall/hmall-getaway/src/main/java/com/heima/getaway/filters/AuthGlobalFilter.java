package com.heima.getaway.filters;

import com.heima.getaway.config.AuthProperties;
import com.heima.getaway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final JwtTool jwtTool;

    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 拿到请求头
        ServerHttpRequest request = exchange.getRequest();
        // 是否需要授权
        // 不需要授权
        if (isExclude(request.getURI().getPath())) {
            return chain.filter(exchange);
        }
        // Authorization
        List<String> authorization = request.getHeaders().get("Authorization");
        // token 为空
        if (authorization == null || authorization.isEmpty()) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        Long userId = null;
        try {
            String token = authorization.get(0);
            userId = jwtTool.parseToken(token);
        } catch (Exception e) {
            // 返回 401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 传递 userId

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public boolean isExclude(String path) {
        for (String excludePath : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(excludePath, path)) {
                return true;
            }
        }
        return false;
    }
}
