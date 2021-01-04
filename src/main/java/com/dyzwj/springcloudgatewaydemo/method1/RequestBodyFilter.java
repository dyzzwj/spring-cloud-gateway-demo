package com.dyzwj.springcloudgatewaydemo.method1;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RequestBodyFilter implements GlobalFilter, Ordered {
    private GatewayFilter delegate;

    public RequestBodyFilter(ModifyRequestBodyGatewayFilterFactory modifyRequestBody, BodyRewrite bodyRewrite) {
        this.delegate = modifyRequestBody.apply(new ModifyRequestBodyGatewayFilterFactory.Config()
                .setRewriteFunction(byte[].class, byte[].class, bodyRewrite));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return delegate.filter(exchange, chain);
    }

    @Override
    public int getOrder() {
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 2;
    }

}


