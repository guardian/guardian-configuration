package com.gu.conf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class PropertiesLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

    private final FileAndResourceLoader loader;
    private final String serviceDomain;

    PropertiesLoader() throws IOException {
        this(new FileAndResourceLoader(), new ServiceDomainProvider());
    }

    PropertiesLoader(FileAndResourceLoader loader, ServiceDomainProvider serviceDomainProvider) throws IOException {
        this.loader = loader;
        this.serviceDomain = serviceDomainProvider.getServiceDomain();

        LOG.info("int.service.domain: " + serviceDomain);
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
        String home = System.getProperty("user.home");
        String propertiesLocation = String.format("file://%s/.gu/%s.properties", home, applicationName);

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
        String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain);

        LOG.info("Loading service domain properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }
}