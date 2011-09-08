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
   private final SetupConfiguration setup;

   public GuardianConfigurationStrategy() throws IOException {
      this(new FileAndResourceLoader());
   }

   GuardianConfigurationStrategy(FileAndResourceLoader loader) throws IOException {
      this(loader, new SetupConfiguration());
   }

   GuardianConfigurationStrategy(FileAndResourceLoader loader, SetupConfiguration setup) throws IOException {
      this.loader = loader;
      this.setup = setup;

      LOG.info("INT_SERVICE_DOMAIN: " + setup.getServiceDomain());
   }

   public Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
      LOG.info("Configuring application {} using classpath configuration directory {}", applicationName, webappConfDirectory);

      AbstractConfiguration properties = CompositeConfiguration.from(
         getDeveloperAccountOverrideProperties(applicationName),
         getOperationsProperties(applicationName),
         getDeveloperStageBasedProperties(webappConfDirectory),
         getDeveloperServiceDomainBasedProperties(webappConfDirectory),
         getDeveloperCommonProperties(webappConfDirectory)
      );

      AbstractConfiguration placeholderProcessed = new PlaceholderProcessingConfiguration(properties);

      LOG.info("Configured webapp {} with properties:\n\n{}", applicationName, placeholderProcessed);

      return placeholderProcessed;
   }

   private AbstractConfiguration getDeveloperAccountOverrideProperties(String applicationName) throws IOException {
      String home = System.getProperty("user.home");
      String propertiesLocation = String.format("file://%s/.gu/%s.properties", home, applicationName);

      LOG.info("Loading developer account override properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getOperationsProperties(String applicationName) throws IOException {
      String propertiesLocation = String.format("file:///etc/gu/%s.properties", applicationName);

      LOG.info("Loading operations properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getDeveloperStageBasedProperties(String confPrefix) throws IOException {
      String stage = setup.getStage();
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, stage);

      LOG.info("Loading developer stage based properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getDeveloperServiceDomainBasedProperties(String confPrefix) throws IOException {
      String serviceDomain = setup.getServiceDomain();
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain);

      LOG.info("Loading developer service domain based properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private AbstractConfiguration getDeveloperCommonProperties(String webappConfDirectory) throws IOException {
      String propertiesLocation = String.format("classpath:%s/global.properties", webappConfDirectory);

      LOG.info("Loading developer common properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

}