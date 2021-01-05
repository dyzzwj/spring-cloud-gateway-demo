package com.dyzwj.springcloudgatewaydemo.config;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class RequestBodyRewriteHandler implements MyRequestHandler {

    @Override
    public boolean support(String channel) {
        return true;
    }


    @Override
    public String handleRequest(String body) {
        log.info("重写前:{}",body);
        Map map = JSON.parseObject(body, Map.class);
        map.put("city","深圳");
        return JSON.toJSONString(map);
    }

    @Override
    public int order() {
        return 0;
    }
}
