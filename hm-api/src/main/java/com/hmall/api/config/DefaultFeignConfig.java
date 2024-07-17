package com.hmall.api.config;

import com.hmall.api.client.fallback.ItemCilentFallbackFactory;
import com.hmall.api.client.fallback.PayClientFallback;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

/**
 * @param
 * @return
 */
public class DefaultFeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel(){
        return  Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 获取登录用户
                Long userId = UserContext.getUser();
                if(userId == null) {
                    // 如果为空则直接跳过
                    return;
                }
                // 如果不为空则放入请求头中，传递给下游微服务
                template.header("user-info", userId.toString());
            }
        };
    }

    @Bean
    public ItemCilentFallbackFactory itemCilentFallbackFactory(){
        return new ItemCilentFallbackFactory();
    }
    @Bean
    public PayClientFallback payClientFallback(){
        return new PayClientFallback();
    }
}
