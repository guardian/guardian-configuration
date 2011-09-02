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
import java.util.Set;

public class GuardianConfigurationStrategy implements ConfigurationStrategy {

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
      Configuration propertyFiles = CompositeConfiguration.from(
         getUserOverrideProperties(applicationName),
         getSysProperties(applicationName),
         getServiceDomainApplicationProperties(webappConfDirectory, applicationName),
         getStageProperties(webappConfDirectory),
         getServiceDomainProperties(webappConfDirectory),
         getGlobalProperties(webappConfDirectory)
      );

      Configuration systemOverrides = propertyFiles.overrideWith(getSystemProperties());

      return new PlaceholderProcessingConfiguration(systemOverrides);
   }

   private Configuration getSystemProperties() {
      LOG.info("Loading override System properties");
      return new SystemPropertiesConfiguration();
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
      String serviceDomain = installation.getServiceDomain();
      String propertiesLocation = String.format("classpath:%s/%s.%s.properties", confPrefix, serviceDomain, applicationName);

      LOG.info("Loading webapp service domain application properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getStageProperties(String confPrefix) throws IOException {
      String stage = installation.getStage();
      String propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, stage);

      LOG.info("Loading webapp service domain properties from " + propertiesLocation);
      return loader.getConfigurationFrom(propertiesLocation);
   }

   private Configuration getServiceDomainProperties(String confPrefix) throws IOException {
      String serviceDomain = installation.getServiceDomain();
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