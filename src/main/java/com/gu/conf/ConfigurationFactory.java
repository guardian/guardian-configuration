package com.gu.conf;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class ConfigurationFactory {
    
    class ResourceLoader {
        InputStream getResource(String resource) throws IOException {
            ClassLoader classloader = ResourceLoader.class.getClassLoader();
            URL url = classloader.getResource(resource);

            return url.openStream();
        }
    }

    private StageResolver stageResolver = new StageResolver();
    private ResourceLoader resourceLoader = new ResourceLoader();

    public Configuration getConfiguration() {
        return getConfiguration("conf");
    }

    public Configuration getConfiguration(String prefix) {
        Properties properties = new Properties();
        String propertiesFile = String.format("%s/%s.properties", prefix,
            stageResolver.getIntServiceDomain());

        InputStream inputStream = null;
        try {
            inputStream = resourceLoader.getResource(propertiesFile);
            properties.load(inputStream);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
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
