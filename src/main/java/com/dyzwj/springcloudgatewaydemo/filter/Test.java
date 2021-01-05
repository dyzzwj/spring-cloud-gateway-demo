package com.dyzwj.springcloudgatewaydemo.filter;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;

//@Component
public class Test implements ApplicationContextAware {

    ApplicationContext applicationContext;

    @PostConstruct
    public void init(){
        System.out.println(applicationContext.getBean(PreGatewayFilterFactory.class));
        System.out.println(applicationContext.getBean(PostGatewayFilterFactory.class));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
