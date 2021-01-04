package com.dyzwj.springcloudgatewaydemo.method1;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


//@Configuration
public class MyConfoguration {

    @Bean
    public BodyRewrite bodyRewrite() {
        return new BodyRewrite();
    }

    /**
     * 定义全局拦截器拦截请求体
     */
    @Bean
    @Order(NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 2)   //指定顺序必须在之前
    public GlobalFilter requestFilter(ModifyRequestBodyGatewayFilterFactory modifyRequestBody) {
        return new RequestBodyFilter(modifyRequestBody,bodyRewrite());
    }

    /**
     * 定义全局拦截器拦截响应体
     */
    @Bean
    @Order(NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 2) //指定顺序必须在之前
    public GlobalFilter responseFilter(ModifyResponseBodyGatewayFilterFactory modifyResponseBody) {

        return new ResponseBodyFilter(modifyResponseBody,bodyRewrite());
    }
}
