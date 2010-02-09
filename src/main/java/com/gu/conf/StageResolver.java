package com.gu.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StageResolver {

    private static final String INSTALLATION_PROPERTIES_FILE = "/etc/gu/installation.properties";

    private ResourceLoader resourceLoader = new ResourceLoader();
    private Properties stageProperties;

    public Properties getStageProperties() {
        if (stageProperties == null) {
            stageProperties = new Properties();

            try {
                InputStream inputStream = resourceLoader.getResource(INSTALLATION_PROPERTIES_FILE);
                stageProperties.load(inputStream);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return stageProperties;
    }

    public String getIntServiceDomain() {
        return getStageProperties().getProperty("int.service.domain");
    }

    void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}