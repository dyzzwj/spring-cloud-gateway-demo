package com.dyzwj.springcloudgatewaydemo.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;


@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    List<MyResponseHandler> myResponseHandlers;

    public List<MyResponseHandler> getMyResponseHandlers() {
        return myResponseHandlers;
    }

    @PostConstruct
    public void init(){
        //排序
        Collections.sort(getMyResponseHandlers(),(h1, h2) -> h1.order() > h2.order() ? 1: -1);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    /**
     *  写body之前 spring给我们一个修改response body的机会
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        System.out.println("postHandle:"+response);
        ServletServerHttpResponse ssResp = (ServletServerHttpResponse)response;
        if( ssResp == null || ssResp.getServletResponse() == null) {
            return body;
        }
        String channel = "ali";
        String result = "";
        if(ssResp.getServletResponse() instanceof ResponseWrapper){
            for (MyResponseHandler myResponseHandler : getMyResponseHandlers()) {
                if(myResponseHandler.support(channel)){
                    result = myResponseHandler.handlerResponse(body);
                }
            }
        }
        return result;
    }
}
