package com.dyzwj.springcloudgatewaydemo.filter1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class CFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("CFilter前置逻辑");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("CFilter后置逻辑");
        }));
    }
}