package com.dyzwj.springcloudgatewaydemo.method1;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class BodyRewrite implements RewriteFunction<byte[], byte[]> {

    /**
     * 在执行全局请求或响应的过滤器的时候会执行该方法，并把请求体或响应体传递进来。
     *
     * @param exchange 网关处理上下文
     * @param body     源请求或响应体
     * @return 返回处理过的请求体或响应体
     */
    @Override
    public Publisher<byte[]> apply(ServerWebExchange exchange, byte[] body) {
        //如果路由没有完成应该是请求过滤器执行
        if (!ServerWebExchangeUtils.isAlreadyRouted(exchange)) {
            System.out.println("请求体："+new String(body));
            exchange.getAttributes().put("request_key", new String(body));  //保存请求体到全局上下文中
            exchange.getAttributes().put("startTime", System.currentTimeMillis()); //保存启动时间到上下中
            //TODO 可以在这里对请求体进行修改
        } else { //已经路由应该是响应过滤器执行
            //TODO 可以在这里对响应体进行修改
            response(exchange, body);
        }
        return Mono.just(body);
    }

    /**
     * 打印输出响应的参数，请求体，响应体，请求头部，响应头部，请求地址，请求方法等。
     *
     * @param exchange 网关处理上下文
     * @param responseBody     源请求或响应体
     * @return 返回处理过的请求体或响应体
     */
    public byte[] response(ServerWebExchange exchange, byte[] responseBody) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String requestbody = exchange.getAttribute("request_key");
            System.out.println("响应体："+new String(responseBody));
            Long startTime = exchange.getAttributeOrDefault("startTime", 0L);
            Long time = System.currentTimeMillis() - startTime;
            boolean flag = MediaType.APPLICATION_JSON.isCompatibleWith(response.getHeaders().getContentType());

            //responseBody=objectMapper.writeValueAsString(MessageBox.ok());
//            log.info("\n[{}]请求地址:\n\t{} {}\n[{}]请求头部:\n{}\n[{}]路径参数:\n{}\n[{}]请求参数:\n{}"
//                            + "\n[{}]响应头部:\n{}\n[{}]响应内容:\n\t{}\n[{}]执行时间[{}]毫秒",
//                    request.getId(), request.getMethod(), request.getPath(),
//                    request.getId(), headers(request.getHeaders()),
//                    request.getId(), request(request),
//                    request.getId(), requestbody,
//                    request.getId(), headers(response.getHeaders()),
//                    request.getId(), flag ? new String(responseBody) : "非JSON字符串不显示",
//                    request.getId(), time);
            //TODO 可以对响应体进行修改
            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException("响应转换错误");
        } finally {
            exchange.getAttributes().remove("request_key");
            exchange.getAttributes().remove("startTime");
        }
    }

    public String headers(HttpHeaders headers) {
        return headers.entrySet().stream()
                .map(entry -> "\t" + entry.getKey() + ": [" + String.join(";", entry.getValue()) + "]")
                .collect(Collectors.joining("\n"));
    }

    /**
     * 处理其它get请求参数
     */
    public String request(ServerHttpRequest request) {
        String params = request.getQueryParams().entrySet().stream()
                .map(entry -> "\t" + entry.getKey() + ": [" + String.join(";", entry.getValue()) + "]")
                .collect(Collectors.joining("\n"));
        return params;
    }
}
