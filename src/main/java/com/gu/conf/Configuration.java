package com.gu.conf;

import java.util.Properties;

public class Configuration {

    private Properties properties = new Properties();

    Configuration(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public String getProperty(String propertyName, String defaultValue) {
        return properties.getProperty(propertyName, defaultValue);
    }

    public Integer getIntegerProperty(String propertyName) {
        return getIntegerProperty(propertyName, null);
    }

    public Integer getIntegerProperty(String propertyName, Integer defaultValue) {
        Integer property = defaultValue;
        try {
            property = Integer.parseInt(getProperty(propertyName));
        } catch (NumberFormatException nfe) {
        }

        return property;
    }
}