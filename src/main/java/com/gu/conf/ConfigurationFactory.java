package com.gu.conf;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ConfigurationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);

    public Configuration getConfiguration(String applicationName) throws IOException {
        return getConfiguration(applicationName, "conf");
    }

    public Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
        LOG.info("Configuring application {} using classpath configuration directory {}",
            applicationName, webappConfDirectory);
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        List<PropertiesWithSource> properties = propertiesLoader.getProperties(applicationName, webappConfDirectory);

        Configuration configuration = new Configuration(properties);
        LOG.info("Configured webapp {} with properties:\n\n{}", applicationName, configuration);

        return configuration;
    }
}
