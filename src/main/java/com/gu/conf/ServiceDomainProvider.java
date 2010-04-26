package com.gu.conf;

import com.gu.conf.exceptions.PropertyNotSetException;
import com.gu.conf.exceptions.UnknownServiceDomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * This class provides a "service domain" value, which indicates
 * the type of server the app is currently executing in.
 */
public class ServiceDomainProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceDomainProvider.class);
    private static final String INSTALLATION_PROPERTIES_LOCATION = "file:///etc/gu/installation.properties";

    private final FileAndResourceLoader loader;

    public ServiceDomainProvider() {
        this(new FileAndResourceLoader());
    }

    public ServiceDomainProvider(FileAndResourceLoader loader) {
        this.loader = loader;
    }

    public String getServiceDomain() {
        String domain = System.getProperty("int.service.domain");
        
        if (domain != null) {
            LOG.info("Service domain overriden by int.service.domain system property to '" + domain + "'");
            return domain;
        }

        LOG.info("Loading installation properties from " + INSTALLATION_PROPERTIES_LOCATION);
        try {
            Properties installationProperties = loader.getPropertiesFrom(INSTALLATION_PROPERTIES_LOCATION);
            domain = installationProperties.getProperty("int.service.domain");

            if (domain == null)
                throw new PropertyNotSetException("int.service.domain", INSTALLATION_PROPERTIES_LOCATION);

            return domain;
        } catch (IOException e) {
            throw new UnknownServiceDomainException("FATAL: could not read " +
                    INSTALLATION_PROPERTIES_LOCATION +
                    "; either create this file (probably using puppet) or set the int.service.domain system property", e);
        } catch (PropertyNotSetException e) {
            throw new UnknownServiceDomainException("FATAL: " + INSTALLATION_PROPERTIES_LOCATION +
                    " exists but does not contain int.service.domain; " +
                    "either update this file (probably using puppet) or set the int.service.domain system property", e);
        }
    }
}
