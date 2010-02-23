package com.gu.conf;

import java.util.Properties;

public class PropertiesBuilder {
    private String source;
    private Properties properties = new Properties();

    public PropertiesWithSource toPropertiesWithSource() {
        return new PropertiesWithSource(properties, source);
    }

    public Properties toProperties() {
        return properties;
    }

    public PropertiesBuilder property(String key, String value) {
        this.properties.setProperty(key, value);
        return this;
    }

    public PropertiesBuilder source(String source) {
        this.source = source;
        return this;
    }
}