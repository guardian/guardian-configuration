package com.gu.conf;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class ConfigurationFactory {

    private static final Logger LOG = Logger.getLogger(ConfigurationFactory.class);

    public Configuration getConfiguration(String applicationName) throws IOException {
        return getConfiguration(applicationName, "conf");
    }

    public Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
        LOG.info(String.format("Configuring application(%s) using classpath configuration directory(%s)",
            applicationName, webappConfDirectory));
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        List<PropertiesWithSource> properties = propertiesLoader.getProperties(applicationName, webappConfDirectory);

        Configuration configuration = new Configuration(properties);
        LOG.info(String.format("Configured webapp %s with properties:\n\n%s", applicationName, configuration));

        return configuration;
    }
}
