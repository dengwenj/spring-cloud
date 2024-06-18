package com.hmall.api.interceptor;

import com.hmall.common.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class UserInfoInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long user = UserContext.getUser();
        if (user != null) {
            requestTemplate.header("userInfo", user.toString());
        }
    }
}
