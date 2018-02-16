package com.activiti.extension.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CustomBean {

    @Autowired
    private Environment environment;

    public String getPropertyValue(String propertyKey) {

        return environment.getProperty(propertyKey);

    }

}
