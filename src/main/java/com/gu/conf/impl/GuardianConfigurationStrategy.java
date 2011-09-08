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

package com.gu.conf.impl;

import com.gu.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GuardianConfigurationStrategy {

   private static final Logger LOG = LoggerFactory.getLogger(GuardianConfigurationStrategy.class);

   private final FileAndResourceLoader loader;
   private final InstallationConfiguration installation;

   public GuardianConfigurationStrategy() throws IOException {
      this(new FileAndResourceLoader());
   }

   GuardianConfigurationStrategy(FileAndResourceLoader loader) throws IOException {
      this(loader, new InstallationConfiguration());
   }

   GuardianConfigurationStrategy(FileAndResourceLoader loader, InstallationConfiguration installation) throws IOException {
      this.loader = loader;
      this.installation = installation;

      LOG.info("INT_SERVICE_DOMAIN: " + installation.getServiceDomain());
   }

   public Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
      LOG.info("Configuring application {} using classpath configuration directory {}", applicationName, webappConfDirectory);

      AbstractConfiguration properties = CompositeConfiguration.from(
         getUserOverrideProperties(applicationName),
         getSysProperties(applicationName),
         getStageProperties(webappConfDirectory),
         getServiceDomainProperties(webappConfDirectory),
         getGlobalProperties(webappConfDirectory)
      );

      AbstractConfiguration placeholderProcessed = new PlaceholderProcessingConfiguration(properties);

      LOG.info("Configured webapp {} with properties:\n\n{}", applicationName, placeholderProcessed);

      return placeholderProcessed;
   }

   private AbstractConfiguration getSystemProperties() {
      LOG.info("Loading override System properties");
      return new SystemPropertiesConfiguration();
   }

   private AbstractConfiguration getUserOverrideProperties(String applicationName) throws IOException {
      String home = System.getProperty("user.home");
      String propertiesLocation = String.format("file://%s/.gu/%s.properties", home, applicationName);

      LOG.info("Loading user override properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getSysProperties(String applicationName) throws IOException {
      String propertiesLocation = String.format("file:///etc/gu/%s.properties", applicationName);

      LOG.info("Loading machine properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getStageProperties(String confPrefix) throws IOException {
      String stage = installation.getStage();
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, stage);

      LOG.info("Loading webapp service domain properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getServiceDomainProperties(String confPrefix) throws IOException {
      String serviceDomain = installation.getServiceDomain();
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain);

      LOG.info("Loading webapp service domain properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getGlobalProperties(String webappConfDirectory) throws IOException {
      String propertiesLocation = String.format("classpath:%s/global.properties", webappConfDirectory);

      LOG.info("Loading webapp global properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

}