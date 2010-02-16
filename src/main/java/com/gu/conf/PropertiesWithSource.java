package com.gu.conf;

import java.util.Properties;

class PropertiesWithSource {

    private Properties properties;
    private PropertiesSource source;

    PropertiesWithSource(Properties properties, PropertiesSource source) {
        this.properties = properties;
        this.source = source;
    }

    String getStringProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    PropertiesSource getSource() {
        return source;
    }
}