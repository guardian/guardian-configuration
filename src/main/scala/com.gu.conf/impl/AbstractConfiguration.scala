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

import com.gu.conf._
import scala.collection.mutable.StringBuilder

private[conf] trait AbstractConfiguration extends Configuration {

  /**
   * Get the source of a property
   *
   * @param propertyName name of the property
   * @return the source of the property or none if the property is unknown
   */
  def getPropertySource(propertyName: String): Option[AbstractConfiguration]

  /**
   * Return a projection of this configuration to the given set of properties
   *
   * @param properties the names of the properties to retain in the projection
   * @return this configuration with only the named properties
   */
  def project(properties: Set[String]): AbstractConfiguration = {
    new ProjectedConfiguration(this, properties)
  }

  /**
   * Return a projection of this configuration to the given set of properties
   *
   * @param properties a configuration containing the names of the properties to retain in the projection
   * @return this configuration with only the given properties
   */
  def project(properties: AbstractConfiguration): AbstractConfiguration = {
    project(properties.getPropertyNames)
  }

  /**
   * Return a copy of this configuration with the the given set of properties removed
   *
   * @param properties the names of the properties to remove
   * @return this configuration with the named properties removed
   */
  def minus(properties: Set[String]): AbstractConfiguration = {
    new ProjectedConfiguration(this, getPropertyNames -- properties)
  }

  /**
   * Return a copy of this configuration with the the given set of properties removed
   *
   * @param properties a configuration containing the names of the properties to remove
   * @return this configuration with the named properties removed
   */
  def minus(properties: AbstractConfiguration): AbstractConfiguration = {
    minus(properties.getPropertyNames)
  }

  /**
   * Return a configuration with the same property names as this configuration but with
   * property values overriden with values from the given configuration.
   *
   * @param overrides a configuration containing the property override values. Properties
   *                  not in the base configuration are ignore.
   * @return this configuration with the given overrides.
   */
  def overrideWith(overrides: AbstractConfiguration): AbstractConfiguration = {
    new CompositeConfiguration(overrides.project(this), this)
  }

  override def toString: String = {
    val builder = new StringBuilder

    builder append "# Properties from "
    builder append getIdentifier
    builder append "\n"

    getPropertyNames.toList.sorted foreach { name =>
      val line = "%s=%s\n" format (name, getPrintableProperty(name).get)
      builder append line
    }

    builder append "\n"

    builder.toString()
  }
}