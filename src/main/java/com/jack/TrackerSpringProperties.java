package com.jack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component
@ConfigurationProperties(prefix = "trackerspringproperties")
@PropertySource("classpath:application.properties")
public class TrackerSpringProperties {

    @Autowired
    private Environment environment;

    private Map<String, String> properties;

    public TrackerSpringProperties( Environment environment) {
        this.environment = environment;
        this.properties = new HashMap<>();

        MutablePropertySources propSrcs = ((AbstractEnvironment) environment).getPropertySources();
        StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::<String>stream)
                .forEach(propName -> properties.put(propName, environment.getProperty(propName)));
    }

    public String get(String propertyName) {
        return environment.getProperty(propertyName);
    }

    /*
     *  Map all properties to a Map<String, String> and return
     */

    public Map<String, String> getAll() {
        return properties;
    }

}
