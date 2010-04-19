package com.gu.conf;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class PropertiesLoader {

    private static final String INSTALLATION_PROPERTIES_LOCATION = "file:///etc/gu/installation.properties";
    private static final Logger LOG = Logger.getLogger(PropertiesLoader.class);

    private FileAndResourceLoader loader;
    private Properties installationProperties;

    PropertiesLoader() throws IOException {
        this(new FileAndResourceLoader());
    }

    PropertiesLoader(FileAndResourceLoader loader) throws IOException {
        this.loader = loader;

        LOG.info("Loading installation properties from " + INSTALLATION_PROPERTIES_LOCATION);
        installationProperties = loader.getPropertiesFrom(INSTALLATION_PROPERTIES_LOCATION);

        LOG.info("int.service.domain: " + getIntServiceDomain());
    }

    String getIntServiceDomain() {
        return installationProperties.getProperty("int.service.domain");
    }

    List<PropertiesWithSource> getProperties(String applicationName, String webappConfDirectory) throws IOException {
        List<PropertiesWithSource> properties = new LinkedList<PropertiesWithSource>();

        PropertiesWithSource overrideProperties = getDevOverrideSysProperties(applicationName);
        if (overrideProperties != null) {
            properties.add(overrideProperties);
        }

        properties.add(getSysProperties(applicationName));
        properties.add(getServiceDomainProperties(webappConfDirectory));
        properties.add(getGlobalProperties(webappConfDirectory));

        return properties;
    }

    private PropertiesWithSource getDevOverrideSysProperties(String applicationName) throws IOException {
        String serviceDomain = getIntServiceDomain();
        if (StringUtils.isBlank(serviceDomain)) {
            String message = "'int.service.domain' variable unavailable from " + INSTALLATION_PROPERTIES_LOCATION;
            LOG.warn(message);

            throw new RuntimeException(message);
        }

        if (!serviceDomain.equals("gudev.gnl")) {         
            return null;
        }

        String home = System.getProperty("user.home");
        String propertiesLocation = String.format("file://%s/.gu/%s.properties", home, applicationName);

        if (!loader.exists(propertiesLocation)) {
            LOG.info("No optional DEV Override sys properties file at " + propertiesLocation);
            return null;
        }

        LOG.info("Loading DEV override sys properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getSysProperties(String applicationName) throws IOException {
        String propertiesLocation = String.format("file:///etc/gu/%s.properties", applicationName);

        LOG.info("Loading sys properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getGlobalProperties(String webappConfDirectory) throws IOException {
        String propertiesLocation = String.format("classpath:%s/global.properties", webappConfDirectory);

        LOG.info("Loading global properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getServiceDomainProperties(String confPrefix) throws IOException {
        String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, getIntServiceDomain());

        LOG.info("Loading service domain properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }
}