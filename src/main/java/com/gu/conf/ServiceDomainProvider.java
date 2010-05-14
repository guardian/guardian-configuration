/*
 * Copyright 2010 Guardian News and Media
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gu.conf;

import com.gu.conf.exceptions.PropertyNotSetException;
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
            LOG.info("unable to find " + INSTALLATION_PROPERTIES_LOCATION + " defaulting to \"default\"");
            return "default";
        } catch (PropertyNotSetException e) {
            LOG.info("unable to find int.service.domain in " + INSTALLATION_PROPERTIES_LOCATION + " defaulting to \"default\"");
            return "default";
        }
    }
}
