package com.hmall.api.cofig;

import com.hmall.api.interceptor.UserInfoInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new UserInfoInterceptor();
    }
}
