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

public class GuardianConfigurationStrategy implements ConfigurationStrategy {

   private static final Logger LOG = LoggerFactory.getLogger(GuardianConfigurationStrategy.class);

   private final FileAndResourceLoader loader;
   private final String serviceDomain;
   private final String stage;

   public GuardianConfigurationStrategy() throws IOException {
      this(new FileAndResourceLoader(), new SystemEnvironmentProvider());
   }

   GuardianConfigurationStrategy(FileAndResourceLoader loader, SystemEnvironmentProvider systemEnvironmentProvider) throws IOException {
      this.loader = loader;
      this.serviceDomain = systemEnvironmentProvider.getServiceDomain();
      this.stage = systemEnvironmentProvider.getStage();

      LOG.info("INT_SERVICE_DOMAIN: " + serviceDomain);
   }


   public Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
      Configuration propertyFiles = CompositeConfiguration.from(
         getUserOverrideProperties(applicationName),
         getSysProperties(applicationName),
         getServiceDomainApplicationProperties(webappConfDirectory, applicationName),
         getStageProperties(webappConfDirectory),
         getServiceDomainProperties(webappConfDirectory),
         getGlobalProperties(webappConfDirectory)
      );

      Configuration systemOverrides = CompositeConfiguration.from(
         getSystemOverrideProperties(propertyFiles),
         propertyFiles
      );

      return new PlaceholderProcessingConfiguration(systemOverrides);
   }

   private Configuration getSystemOverrideProperties(Configuration existing) {
      Set<String> keys = existing.getPropertyNames();

      LOG.info("Loading override System properties");
      return new SystemPropertiesConfiguration().project(keys);
   }

   private Configuration getUserOverrideProperties(String applicationName) throws IOException {
      String home = System.getProperty("user.home");
      String propertiesLocation = String.format("file://%s/.gu/%s.properties", home, applicationName);

      LOG.info("Loading user override properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getSysProperties(String applicationName) throws IOException {
      String propertiesLocation = String.format("file:///etc/gu/%s.properties", applicationName);

      LOG.info("Loading machine properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getServiceDomainApplicationProperties(String confPrefix, String applicationName) throws IOException {
      String propertiesLocation = String.format("classpath:%s/%s.%s.properties", confPrefix, serviceDomain, applicationName);

      LOG.info("Loading webapp service domain application properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getStageProperties(String confPrefix) throws IOException {
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, stage);

      LOG.info("Loading webapp service domain properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getServiceDomainProperties(String confPrefix) throws IOException {
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain);

      LOG.info("Loading webapp service domain properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getGlobalProperties(String webappConfDirectory) throws IOException {
      String propertiesLocation = String.format("classpath:%s/global.properties", webappConfDirectory);

      LOG.info("Loading webapp global properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

}