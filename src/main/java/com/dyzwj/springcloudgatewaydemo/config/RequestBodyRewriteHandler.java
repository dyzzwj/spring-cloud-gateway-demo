package com.dyzwj.springcloudgatewaydemo.config;


import com.alibaba.fastjson.JSON;
import com.dyzwj.customgateway.po.DataBase;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestBodyRewriteHandler implements MyRequestHandler {

    @Override
    public boolean support(String channel) {
        return true;
    }

    @Override
    public void handleRequest(RequestWrapper requestWrapper) {
        String channel = "ali";
        String originBody = null;
        try {
            originBody = readBodyFromRequest(requestWrapper.getOriginalRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,Object> newBody = new HashMap<>();
        Map object = JSON.parseObject(originBody, Map.class);
        Map<String, Object> stringObjectMap = DataBase.META_DATA.get(channel);
        stringObjectMap.entrySet().forEach(entry ->{
            newBody.put(entry.getKey(),object.get(entry.getValue()));
        });
        System.out.println("new body:" + newBody);
        requestWrapper.setBody(JSON.toJSONString(newBody));;
    }

    private String readBodyFromRequest(HttpServletRequest request) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }
        return stringBuilder.toString();

    }

    @Override
    public int order() {
        return 0;
    }
}
