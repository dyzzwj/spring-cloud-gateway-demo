package com.dyzwj.springcloudgatewaydemo.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;


/**
 * 解密
 */
@Component
@Slf4j
public class RequestBodyDecryptHandler implements MyRequestHandler {

    @Override
    public String handleRequest(String body) {
        log.info("解密前:{}",body);
        Map object = JSON.parseObject(body, Map.class);
        byte[] decode = Base64.getDecoder().decode((String) object.get("data"));
        log.info("解密后:{}",new String(decode));

        return new String(decode);
    }


    @Override
    public int order() {
        return -1;
    }
}
