package com.mjc.school.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="newsdata.api")
public class NewDataApiPropertiesConfig {

    private String key;
    public String getKey(){return key;}
    public void setKey(String key){this.key = key;}

}
