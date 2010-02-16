package com.gu.conf;

import org.apache.log4j.Logger;

import java.util.List;

public class ConfigurationFactory {

    private static final Logger LOG = Logger.getLogger(ConfigurationFactory.class);

    public Configuration getConfiguration(String applicationName) {
        return getConfiguration(applicationName, "conf");
    }

    public Configuration getConfiguration(String applicationName, String webappConfDirectory) {
        LOG.info(String.format("Configuring webapp %s using in archive configuration directory %s",
            applicationName, webappConfDirectory));
        PropertiesLoader propertiesLocator = new PropertiesLoader();
        List<PropertiesWithSource> properties = propertiesLocator.getProperties(applicationName, webappConfDirectory);

        Configuration configuration = new Configuration(properties);
        LOG.info(String.format("Configured webapp %s with properties:\n%s", applicationName, configuration));

        return configuration;
    }
}
