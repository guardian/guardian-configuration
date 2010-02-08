package com.gu.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationFactory {

    private LocalHostResolver resolver = new LocalHostResolver();
    private ResourceLoader resourceLoader = new ResourceLoader();

    public Configuration getConfiguration() {
        return getConfiguration("conf");
    }

    public Configuration getConfiguration(String prefix) {
        Properties properties = new Properties();

        try {
            String propertiesFile = String.format("%s/%s.properties", prefix,
                    resolver.getLocalHostDomain());
            InputStream inputStream = resourceLoader.getResource(propertiesFile);

            properties.load(inputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return new Configuration(properties);
    }

    void setResolver(LocalHostResolver resolver) {
        this.resolver = resolver;
    }

    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}