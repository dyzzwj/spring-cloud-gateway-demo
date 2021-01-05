package com.dyzwj.springcloudgatewaydemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密
 */
@Component
@Slf4j
public class ResponseBodyEncryptionHandler implements MyResponseHandler {

    @Override
    public String handlerResponse(String body) {
        return Base64.getEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int order() {
        return 0;
    }
}
