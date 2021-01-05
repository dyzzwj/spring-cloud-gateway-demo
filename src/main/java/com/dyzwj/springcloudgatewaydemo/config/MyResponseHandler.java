package com.dyzwj.springcloudgatewaydemo.config;

/**
 * 响应处理handler
 */
public interface MyResponseHandler extends MyHandler{

    /**
     * 处理响应
     * @param
     */
    String handlerResponse(String body);

}
