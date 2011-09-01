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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides information on the the nature of the environment that the machine forms a part of
 */
public class SystemEnvironmentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SystemEnvironmentProvider.class);
    private static final String INSTALLATION_PROPERTIES_LOCATION = "file:///etc/gu/install_vars";

    private final FileAndResourceLoader loader;

    public SystemEnvironmentProvider() {
        this(new FileAndResourceLoader());
    }

    public SystemEnvironmentProvider(FileAndResourceLoader loader) {
        this.loader = loader;
    }

    public String getServiceDomain() {
        return getProperty("int.service.domain", "INT_SERVICE_DOMAIN");
    }

    public String getStage() {
        return getProperty("stage", "STAGE");
    }

    private String getProperty(String systemPropertyName, String installationPropertyName) {
        String property = System.getProperty(systemPropertyName);

        if (property != null) {
            LOG.info("Property overriden by " + systemPropertyName +" system property to '" + property + "'");
            return property;
        }

        LOG.info("Loading installation properties from " + INSTALLATION_PROPERTIES_LOCATION);

        Configuration installationProperties = loader.getConfigurationFrom(INSTALLATION_PROPERTIES_LOCATION);
        property = installationProperties.getStringProperty(installationPropertyName, null);

        if (property == null) {
            LOG.info("unable to find " + installationPropertyName + " in " + INSTALLATION_PROPERTIES_LOCATION + " defaulting to \"default\"");
            return "default";
        }

        return property;
    }
}
