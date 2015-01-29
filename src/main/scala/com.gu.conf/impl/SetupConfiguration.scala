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
package com.gu.conf.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private[conf] class SetupConfiguration(
    val loader: PropertiesLoader = new PropertiesLoader) extends AbstractConfiguration {

  private val stageDelimiter = "___"
  private val LOG: Logger = LoggerFactory.getLogger(classOf[SetupConfiguration])
  val SETUP_PROPERTIES_LOCATION: String = "file:///etc/gu/install_vars"

  LOG.info("Loading setup properties from " + SETUP_PROPERTIES_LOCATION)
  val properties = loader getPropertiesFrom SETUP_PROPERTIES_LOCATION

  val setup = new PropertiesBasedConfiguration(SETUP_PROPERTIES_LOCATION, properties)

  def getIdentifier: String = "Setup"

  def getServiceDomain: String = {
    getStringProperty("int.service.domain") orElse
      getStringProperty("INT_SERVICE_DOMAIN") getOrElse {
        LOG.info("unable to find INT_SERVICE_DOMAIN in " + SETUP_PROPERTIES_LOCATION + " defaulting to \"default\"")
        "default"
      }
  }

  def getStage: String = {
    getStringProperty("stage") orElse
      getStringProperty("STAGE") map(translateIfDelimited(_)) getOrElse {
        LOG.info("unable to find STAGE in " + SETUP_PROPERTIES_LOCATION + " defaulting to \"default\"")
        "default"
      }
  }

  private[this] def translateIfDelimited(stage: String): String = {
    if (stage.contains(stageDelimiter))
      stage.split(stageDelimiter).head
    else stage
  }

  def getPropertySource(propertyName: String): Option[AbstractConfiguration] = {
    setup.getPropertySource(propertyName)
  }

  def getStringProperty(propertyName: String): Option[String] = {
    setup.getStringProperty(propertyName)
  }

  def getPropertyNames: Set[String] = setup.getPropertyNames

  def getEnvironmentVariables: Map[String, String] = {
    import scala.collection.JavaConversions._
    System.getenv.toMap
  }

  override def toString(): String = setup.toString()
}