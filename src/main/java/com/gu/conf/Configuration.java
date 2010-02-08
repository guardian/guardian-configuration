package com.gu.conf;

import java.util.Properties;

public class Configuration {

    private Properties properties = new Properties();

    Configuration(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    public String getProperty(String property, String defaultValue) {
        return properties.getProperty(property, defaultValue);
    }
}