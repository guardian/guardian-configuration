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

import java.io.IOException;
import java.util.*;

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

        LOG.info("INT_SERVICE_DOMAIN: " + serviceDomain);
    }

    List<PropertiesWithSource> getProperties(String applicationName, String webappConfDirectory) throws IOException {
        LinkedList<PropertiesWithSource> properties = new LinkedList<PropertiesWithSource>();

        properties.add(getUserOverrideProperties(applicationName));
        properties.add(getSysProperties(applicationName));
        properties.add(getServiceDomainProperties(webappConfDirectory));
        properties.add(getGlobalProperties(webappConfDirectory));
        properties.addFirst(getSystemOverrideProperties(getAllPropertyKeys(properties)));

        return properties;
    }

    private PropertiesWithSource getUserOverrideProperties(String applicationName) throws IOException {
        String home = System.getProperty("user.home");
        String propertiesLocation = String.format("file://%s/.gu/%s.properties", home, applicationName);

        LOG.info("Loading user override properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getSysProperties(String applicationName) throws IOException {
        String propertiesLocation = String.format("file:///etc/gu/%s.properties", applicationName);

        LOG.info("Loading machine properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getGlobalProperties(String webappConfDirectory) throws IOException {
        String propertiesLocation = String.format("classpath:%s/global.properties", webappConfDirectory);

        LOG.info("Loading webapp global properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getServiceDomainProperties(String confPrefix) throws IOException {
        String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain);

        LOG.info("Loading webapp service domain properties from " + propertiesLocation);
        Properties properties = loader.getPropertiesFrom(propertiesLocation);

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private PropertiesWithSource getSystemOverrideProperties(List<Object> keys) {
        Properties properties = new Properties();
        String propertiesLocation = "System";

        LOG.info("Loading override System properties");
        for (Object key : keys) {
            if (System.getProperties().containsKey(key)) {
                String value = System.getProperty((String) key);
                properties.setProperty((String) key, value);
            }
        }

        return new PropertiesWithSource(properties, propertiesLocation);
    }

    private List<Object> getAllPropertyKeys(List<PropertiesWithSource> properties) {
        List<Object> keys = new ArrayList<Object>();

        for (PropertiesWithSource propertiesWithSource : properties) {
            keys.addAll(propertiesWithSource.propertyKeys());
        }

        return keys;
    }
}