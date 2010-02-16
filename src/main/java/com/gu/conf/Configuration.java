package com.gu.conf;

import java.util.List;

public class Configuration {

    private List<PropertiesWithSource> properties;

    Configuration(List<PropertiesWithSource> properties) {
        this.properties = properties;
    }

    public PropertiesSource getPropertySource(String propertyName) {
        for (PropertiesWithSource props : properties) {
            if (props.getStringProperty(propertyName) != null) {
                return props.getSource();
            }
        }

        return null;
    }

    public String getStringProperty(String propertyName) {
        return getStringProperty(propertyName, null);
    }

    public String getStringProperty(String propertyName, String defaultValue) {
        for (PropertiesWithSource props : properties) {
            String property = props.getStringProperty(propertyName);
            if (property != null) {
                return property;
            }
        }

        return defaultValue;
    }

    public Integer getIntegerProperty(String propertyName) {
        return getIntegerProperty(propertyName, null);
    }

    public Integer getIntegerProperty(String propertyName, Integer defaultValue) {
        Integer property = defaultValue;
        try {
            property = Integer.parseInt(getStringProperty(propertyName));
        } catch (NumberFormatException nfe) {
        }

        return property;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (PropertiesWithSource props : properties) {
            stringBuilder.append(props);
        }

        return stringBuilder.toString();
    }
}