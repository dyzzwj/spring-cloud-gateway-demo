package com.dyzwj.springcloudgatewaydemo.config;

/**
 * 请求处理handler
 */
public interface MyRequestHandler extends MyHandler{

    /**
     * 处理请求
     * @param requestWrapper
     */
    void handleRequest(RequestWrapper requestWrapper);

}
