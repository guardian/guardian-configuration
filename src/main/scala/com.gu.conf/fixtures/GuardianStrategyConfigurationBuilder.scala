/*
 * Copyright 2011 Guardian News and Media
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
package com.gu.conf.fixtures

import com.gu.conf.{ GuardianConfigurationStrategy, Configuration }
import com.gu.conf.impl.{ PropertiesLoader, SetupConfiguration }
import java.util.Properties
import org.slf4j.{ LoggerFactory, Logger }

class GuardianStrategyConfigurationBuilder {

  private val LOG: Logger = LoggerFactory.getLogger(classOf[GuardianStrategyConfigurationBuilder])

  private var stage: String = "PROD"
  private var serviceDomain: String = "gutc.gnl"

  def stage(_stage: String): GuardianStrategyConfigurationBuilder = {
    this.stage = _stage
    this
  }

  def serviceDomain(_serviceDomain: String): GuardianStrategyConfigurationBuilder = {
    this.serviceDomain = _serviceDomain
    this
  }

  def toConfiguration(applicationName: String, webappConfDirectory: String = "conf"): Configuration = {
    val properties = new PropertiesBuilder().
      property("int.service.domain", serviceDomain).
      property("stage", stage).
      toProperties

    val propertiesLoader = new PropertiesLoader() {
      override def getPropertiesFrom(descriptor: String): Properties = {
        LOG.info("Overriding setup properties load from " + descriptor)
        properties
      }
    }

    val setupConfiguration = new SetupConfiguration(propertiesLoader)
    val strategy = new GuardianConfigurationStrategy(new PropertiesLoader, setupConfiguration)

    strategy.getConfiguration(applicationName, webappConfDirectory)
  }
}