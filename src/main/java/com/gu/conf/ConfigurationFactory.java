package com.gu.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationFactory {

    private StageResolver stageResolver = new StageResolver();
    private ResourceLoader resourceLoader = new ResourceLoader();

    public Configuration getConfiguration() {
        return getConfiguration("conf");
    }

    public Configuration getConfiguration(String prefix) {
        Properties properties = new Properties();

        try {
            String propertiesFile = String.format("%s/%s.properties", prefix,
                    stageResolver.getIntServiceDomain());
            InputStream inputStream = resourceLoader.getResource(propertiesFile);
            properties.load(inputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return new Configuration(properties);
    }

    void setStageResolver(StageResolver stageResolver) {
        this.stageResolver = stageResolver;
    }

    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}