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
package com.gu.conf

import com.gu.conf.impl._
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties

private[conf] class GuardianConfigurationStrategy(
    val loader: PropertiesLoader = new PropertiesLoader,
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
      getDeveloperCommonProperties(webappConfDirectory),
      getEnvironmentProperties)

    val placeholderProcessed = new PlaceholderProcessingConfiguration(properties)

    LOG.info("Configured webapp {} with properties:\n\n{}", applicationName, placeholderProcessed)

    placeholderProcessed
  }

  def getDeveloperAccountOverrideProperties(applicationName: String) = {
    val home = System getProperty "user.home"
    val location = "file://%s/.gu/%s.properties".format(home, applicationName)

    LOG.info("Loading developer account override properties from " + location)
    val properties = loader getPropertiesFrom location

    new PropertiesBasedConfiguration(location, properties)
  }

  def getOperationsProperties(applicationName: String) = {
    val location = "file:///etc/gu/%s.properties" format applicationName

    LOG.info("Loading operations properties from " + location)
    val properties = loader getPropertiesFrom location

    new PropertiesBasedConfiguration(location, properties)
  }

  def getDeveloperStageBasedProperties(confPrefix: String) = {
    val stage = setup.getStage
    val location = "classpath:%s/%s.properties".format(confPrefix, stage)

    LOG.info("Loading developer stage based properties from " + location)
    val properties = loader getPropertiesFrom location

    new PropertiesBasedConfiguration(location, properties)
  }

  def getDeveloperServiceDomainBasedProperties(confPrefix: String) = {
    val serviceDomain = setup.getServiceDomain
    val location = String.format("classpath:%s/%s.properties", confPrefix, serviceDomain)

    LOG.info("Loading developer service domain based properties from " + location)
    val properties = loader getPropertiesFrom location

    new PropertiesBasedConfiguration(location, properties)
  }

  def getDeveloperCommonProperties(webappConfDirectory: String) = {
    val location = "classpath:%s/global.properties" format webappConfDirectory

    LOG.info("Loading developer common properties from " + location)
    val properties = loader getPropertiesFrom location

    new PropertiesBasedConfiguration(location, properties)
  }

  def getEnvironmentProperties = {
    LOG.info("Loading system environment variables")
    val props = new Properties()

    setup.getEnvironmentVariables.foreach { pair => props.setProperty(pair._1, pair._2) }

    new PropertiesBasedConfiguration("Environment", props)
  }

}
