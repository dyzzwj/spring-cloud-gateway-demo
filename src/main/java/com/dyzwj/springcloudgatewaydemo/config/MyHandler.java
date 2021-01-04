package com.dyzwj.springcloudgatewaydemo.config;

public interface MyHandler {

    /**
     * 支持的渠道
     * @param channel
     * @return
     */
    default boolean support(String channel){
        return true;
    }

    /**
     * 执行顺序
     * @return
     */
    int order();

}
