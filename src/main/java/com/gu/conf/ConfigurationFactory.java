package com.gu.conf;

import java.util.List;

public class ConfigurationFactory {

    public Configuration getConfiguration(String applicationName) {
        return getConfiguration(applicationName, "conf");
    }

    public Configuration getConfiguration(String applicationName, String webappConfDirectory) {
        PropertiesLoader propertiesLocator = new PropertiesLoader();
        List<PropertiesWithSource> properties = propertiesLocator.getProperties(applicationName, webappConfDirectory);

        return new Configuration(properties);
    }
}
