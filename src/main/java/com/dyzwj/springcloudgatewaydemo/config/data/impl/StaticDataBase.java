package com.dyzwj.springcloudgatewaydemo.config.data.impl;

import com.dyzwj.springcloudgatewaydemo.config.data.DataBase;

import java.util.HashMap;
import java.util.Map;


public class StaticDataBase implements DataBase {

    public static Map<String, Object> META_DATA = new HashMap<>();

    static {
        Map<String,Object> ali = new HashMap<>();
        ali.put("username","name");
        ali.put("password","pwd");

        Map<String,Object> tencent = new HashMap<>();
        tencent.put("username","userName");
        tencent.put("password","passwd");


        META_DATA.put("ali",ali);
        META_DATA.put("tencent",tencent);
    }

    @Override
    public Map<String, Object> getData() {
        return META_DATA;
    }
}

