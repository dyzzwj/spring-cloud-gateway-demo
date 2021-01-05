package com.dyzwj.springcloudgatewaydemo.filter1;

import com.dyzwj.springcloudgatewaydemo.config.MyRequestHandler;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class PreGatewayFilter implements GlobalFilter, Ordered {


    @Autowired
    private List<MyRequestHandler> myRequestHandlers;


    public List<MyRequestHandler> getMyRequestHandlers() {
        return myRequestHandlers;
    }

    @PostConstruct
    public void init() {
        log.info("排序前:{}", getMyRequestHandlers());
        //对handler排序 降序
        Collections.sort(getMyRequestHandlers(), (h1, h2) -> h1.order() > h2.order() ? 1 : -1);
        log.info("排序后:{}", getMyRequestHandlers());
    }


    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (request.getMethod() != HttpMethod.POST) {
            return chain.filter(exchange);
        }
        return operationExchange(exchange, chain);
    }

    private Mono<Void> operationExchange(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        URI ex = UriComponentsBuilder.fromUri(uri).build(true).toUri();
        ServerHttpRequest request = exchange.getRequest().mutate().uri(ex).build();
        if ("POST".equalsIgnoreCase(request.getMethodValue())) {//判断是否为POST请求
            Flux<DataBuffer> body = request.getBody();
            AtomicReference<String> bodyRef = new AtomicReference<>();//缓存读取的request body信息
            body.subscribe(dataBuffer -> {
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
                DataBufferUtils.release(dataBuffer);
                bodyRef.set(charBuffer.toString());
            });//读取request body到缓存
            String bodyStr = bodyRef.get();//获取request body
            System.out.println(bodyStr);//这里是我们需要做的操作
            bodyStr = rewriteRequestBody(bodyStr);
            DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
            //解决request body只能读取一次的问题
            request = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };//封装我们的request
        }
        return chain.filter(exchange.mutate().request(request).build());
    }


    protected DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }


//    private Mono<Void> operationExchange(ServerWebExchange exchange, GatewayFilterChain chain) {
//        // mediaType
//        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
//        // read & modify body
//        ServerRequest serverRequest = new DefaultServerRequest(exchange);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.putAll(exchange.getRequest().getHeaders());
//        headers.remove(HttpHeaders.CONTENT_LENGTH);
//        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
//        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
//                .flatMap(body -> {
//                    if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
//                        // 对原先的body进行修改操作
////                        String newBody = "{\"testName\":\"testValue\"}";
//                        return Mono.just(rewriteRequestBody(outputMessage.getBody().toString()));
////                        return Mono.just(newBody);
//                    }
//                    return Mono.empty();
//                });
//        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
//
//        return bodyInserter.insert(outputMessage, new BodyInserterContext())
//                .then(Mono.defer(() -> {
//                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
//                            exchange.getRequest()) {
//                        @Override
//                        public HttpHeaders getHeaders() {
//                            long contentLength = headers.getContentLength();
//                            HttpHeaders httpHeaders = new HttpHeaders();
//                            httpHeaders.putAll(super.getHeaders());
//                            if (contentLength > 0) {
//                                httpHeaders.setContentLength(contentLength);
//                            } else {
//                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
//                            }
//                            return httpHeaders;
//                        }
//
//                        @Override
//                        public Flux<DataBuffer> getBody() {
//                            return outputMessage.getBody();
//                        }
//                    };
//                    return chain.filter(exchange.mutate().request(decorator).build());
////                    return chain.filter(exchange.mutate().request(decorator).build()).then(Mono.fromRunnable(() ->{
////                        rewriteResponseBody(exchange.getResponse());
////                    }));
//
//                }));
//    }


    /**
     * 修改request body
     *
     * @param body
     * @return
     */
    private String rewriteRequestBody(String body) {

        String channel = "";

        log.info("original request body:{}", body);
        for (MyRequestHandler myRequestHandler : getMyRequestHandlers()) {
            if (myRequestHandler.support(channel)) {
                body = myRequestHandler.handleRequest(body);
            }
        }
        log.info("new request body:{}", body);
        return body;
    }

    /**
     * 获取请求体中的字符串内容
     *
     * @param serverHttpRequest
     * @return
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder sb = new StringBuilder();

        body.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        return sb.toString();

    }
}