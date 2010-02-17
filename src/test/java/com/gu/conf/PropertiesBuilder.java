package com.gu.conf;

import java.util.Properties;

public class PropertiesBuilder {
    private PropertiesSource source;
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

    public PropertiesBuilder source(PropertiesSource source) {
        this.source = source;
        return this;
    }

    public PropertiesBuilder devOverrideSystemWebappProperties() {
        return source(PropertiesSource.DEV_SYSTEM_WEBAPP_PROPERTIES);
    }

    public PropertiesBuilder systemWebappProperties() {
        return source(PropertiesSource.SYSTEM_WEBAPP_PROPERTIES);
    }

    public PropertiesBuilder webappGlobalProperties() {
        return source(PropertiesSource.WEBAPP_GLOBAL_PROPERTIES);
    }

    public PropertiesBuilder webappStageProperties() {
        return source(PropertiesSource.WEBAPP_STAGE_PROPERTIES);
    }
}