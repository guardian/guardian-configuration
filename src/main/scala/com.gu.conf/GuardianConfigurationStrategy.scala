package com.gu.conf

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

import com.gu.conf.impl._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private[conf] class GuardianConfigurationStrategy(
  val loader: FileAndResourceLoader = new FileAndResourceLoader,
  val setup: SetupConfiguration = new SetupConfiguration) {

  private final val LOG: Logger = LoggerFactory.getLogger(classOf[GuardianConfigurationStrategy])

  LOG.info("INT_SERVICE_DOMAIN: " + setup.getServiceDomain)

  def getConfiguration(applicationName: String, webappConfDirectory: String): Configuration = {
    LOG.info("Configuring application {} using classpath configuration directory {}", applicationName, webappConfDirectory)

    val properties = CompositeConfiguration.from(
      getDeveloperAccountOverrideProperties(applicationName),
      getOperationsProperties(applicationName),
      getDeveloperStageBasedProperties(webappConfDirectory),
      getDeveloperServiceDomainBasedProperties(webappConfDirectory),
      getDeveloperCommonProperties(webappConfDirectory))

    val placeholderProcessed = new PlaceholderProcessingConfiguration(properties)

    LOG.info("Configured webapp {} with properties:\n\n{}", applicationName, placeholderProcessed)

    placeholderProcessed
  }

  def getDeveloperAccountOverrideProperties(applicationName: String) = {
    val home = System getProperty "user.home"
    val propertiesLocation = "file://%s/.gu/%s.properties".format(home, applicationName)

    LOG.info("Loading developer account override properties from " + propertiesLocation)

    loader getConfigurationFrom propertiesLocation
  }

  def getOperationsProperties(applicationName: String) = {
    val propertiesLocation = "file:///etc/gu/%s.properties" format applicationName

    LOG.info("Loading operations properties from " + propertiesLocation)

    loader getConfigurationFrom propertiesLocation
  }

  def getDeveloperStageBasedProperties(confPrefix: String) = {
    val stage = setup.getStage
    val propertiesLocation = "classpath:%s/%s.properties".format(confPrefix, stage)

    LOG.info("Loading developer stage based properties from " + propertiesLocation)

    loader getConfigurationFrom propertiesLocation
  }

  def getDeveloperServiceDomainBasedProperties(confPrefix: String) = {
    val serviceDomain = setup.getServiceDomain
    val propertiesLocation = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain)

    LOG.info("Loading developer service domain based properties from " + propertiesLocation)

    loader getConfigurationFrom propertiesLocation
  }

  def getDeveloperCommonProperties(webappConfDirectory: String) = {
    val propertiesLocation = "classpath:%s/global.properties" format webappConfDirectory

    LOG.info("Loading developer common properties from " + propertiesLocation)

    loader getConfigurationFrom propertiesLocation
  }
}