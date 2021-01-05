package com.dyzwj.springcloudgatewaydemo.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ResponseBodyRewriteHandler implements MyResponseHandler {
    @Override
    public String handlerResponse(String body) {
        Map map = JSON.parseObject(body, Map.class);
        map.put("weather","sunhine");
        return JSON.toJSONString(map);
    }

    @Override
    public int order() {
        return -1;
    }
}
