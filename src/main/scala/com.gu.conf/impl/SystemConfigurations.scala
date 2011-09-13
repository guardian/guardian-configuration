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

import java.util.Properties
import scala.collection.JavaConversions._

private[conf] object SystemEnvironmentConfiguration {
  val environment = System.getenv().toMap
}

private[conf] class SystemEnvironmentConfiguration(
  private val _identifier: String = "Environment",
  private val _properties: Map[String, String] = SystemEnvironmentConfiguration.environment) extends MapBasedConfiguration(_identifier, _properties)

private[conf] class SystemPropertiesConfiguration(
  private val _identifier: String = "System",
  private val _properties: Properties = System.getProperties) extends PropertiesBasedConfiguration(_identifier, _properties)
