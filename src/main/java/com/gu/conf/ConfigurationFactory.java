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
import java.util.List;

public class ConfigurationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);

    public static Configuration getConfiguration(String applicationName) throws IOException {
        return getConfiguration(applicationName, "conf");
    }

    public static Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
        LOG.info("Configuring application {} using classpath configuration directory {}",
            applicationName, webappConfDirectory);
        PropertiesLoader propertiesLoader = new PropertiesLoader();
        List<PropertiesWithSource> properties = propertiesLoader.getProperties(applicationName, webappConfDirectory);
        properties = new PlaceholderProcessor().resolvePlaceholders(properties);

        Configuration configuration = new PropertiesFileBasedConfiguration(properties);
        LOG.info("Configured webapp {} with properties:\n\n{}", applicationName, configuration);

        return configuration;
    }
}
