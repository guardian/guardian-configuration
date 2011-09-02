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
public class InstallationConfiguration extends ConfigurationProxy {
   private static final Logger LOG = LoggerFactory.getLogger(InstallationConfiguration.class);
   public static final String INSTALLATION_PROPERTIES_LOCATION = "file:///etc/gu/install_vars";

   public InstallationConfiguration() {
      this(new FileAndResourceLoader());
   }

   InstallationConfiguration(FileAndResourceLoader loader) {
      LOG.info("Loading installation properties from " + INSTALLATION_PROPERTIES_LOCATION);
      Configuration installationProperties = loader.getConfigurationFrom(INSTALLATION_PROPERTIES_LOCATION);

      Configuration installation = CompositeConfiguration.from(
         new SystemPropertiesConfiguration(),
         installationProperties
      );

      setDelegate(installation);
   }

   public String getIdentifier() {
      return "Installation";
   }

   public String getServiceDomain() {
      String property = getStringProperty("int.service.domain", null);
      if (property == null) {
         property = getStringProperty("INT_SERVICE_DOMAIN", null);
      }

      if (property == null) {
         LOG.info("unable to find INT_SERVICE_DOMAIN in " + INSTALLATION_PROPERTIES_LOCATION + " defaulting to \"default\"");
         property = "default";
      }

      return property;
   }

   public String getStage() {
      String property = getStringProperty("stage", null);
      if (property == null) {
         property = getStringProperty("STAGE", null);
      }

      if (property == null) {
         LOG.info("unable to find STAGE in " + INSTALLATION_PROPERTIES_LOCATION + " defaulting to \"default\"");
         property = "default";
      }

      return property;
   }
}
