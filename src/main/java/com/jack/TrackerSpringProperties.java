package com.jack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "trackerspringproperties")
@PropertySource("classpath:application.properties")
public class TrackerSpringProperties {

    @Autowired
    private Environment environment;

    public String get(String propertyName) {
        return environment.getProperty(propertyName);
    }
}
