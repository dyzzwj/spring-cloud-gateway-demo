package com.dyzwj.springcloudgatewaydemo.config;


/**
 * 请求处理handler
 */
public interface MyRequestHandler extends MyHandler{

    /**
     * 处理请求
     * @param body
     */
    String handleRequest(String body);

}
