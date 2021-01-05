package com.dyzwj.springcloudgatewaydemo.filter;

import com.dyzwj.springcloudgatewaydemo.config.MyRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
//@Component
public class PreGatewayFilterFactory extends AbstractGatewayFilterFactory<PreGatewayFilterFactory.Config> {

    //    @Autowired
    List<MyRequestHandler> myRequestHandlers;

    public PreGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // grab configuration from Config object
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//
//            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
//            //use builder to manipulate the request
//            return chain.filter(exchange.mutate().request(request).build());
//        };

        return new GatewayFilter() {
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                ServerHttpRequest request = exchange.getRequest();

                ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
                //use builder to manipulate the request
                return chain.filter(exchange.mutate().request(request).build());
            }
        };

    }

    public static class Config {
        //Put the configuration properties for your filter here
    }

}
