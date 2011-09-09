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

import com.gu.conf.impl.GuardianConfigurationStrategy
import java.util.Properties

trait Configuration {

  /**
   * Get an identifier for this configuration. Used for identifying source of property in composite
   * based configurations.
   *
   * @return identifier for this configuration
   */
  def getIdentifier: String

  /**
   * Return true if configuration has a property with the given name
   *
   * @param propertyName name of the property
   * @return true if configuration contains named property
   */
  def hasProperty(propertyName: String): Boolean

  /**
   * Return a set of property names
   *
   * @return set of property names
   */
  def getPropertyNames: Set[String]

  /**
   * Return the value of property
   *
   * @param propertyName name of the property
   * @return value of the property
   * @throws PropertyNotSetException if property has not been set
   */
  def getStringProperty(propertyName: String): Option[String]

  /**
   * Return the value of property, or default if property is not set
   *
   * @param propertyName name of the property
   * @param defaultValue value to return if property not set
   * @return value of the property or defaultValue if property not set
   */
  def getStringProperty(propertyName: String, defaultValue: String): String

  /**
   * Returns the value of a property converted to an int
   *
   * @param propertyName name of the property
   * @return integer value
   * @throws PropertyNotSetException if property has not been set
   * @throws NumberFormatException   if the property was found but was not an integer
   */
  def getIntegerProperty(propertyName: String): Option[Int]

  /**
   * Returns the value of a property converted to an int, or a default value if property is not found
   * or is not an integer
   *
   * @param propertyName name of the property
   * @param defaultValue value to return if property not set
   * @return alue of the property or defaultValue if property not set or not an integer
   */
  def getIntegerProperty(propertyName: String, defaultValue: Int): Int

  /**
   * Return the value of property
   *
   * @param propertyName name of the property
   * @return value of the property
   * @throws PropertyNotSetException if property has not been set
   */
  def getStringPropertiesSplitByComma(propertyName: String): List[String]

  /**
   * Return a properties version of this configuration
   *
   * @return this configuration as a java.util.Properties object
   */
  def toProperties: Properties
}

object ConfigurationFactory {
  def getConfiguration(applicationName: String): Configuration = {
    getConfiguration(applicationName, "conf")
  }

  def getConfiguration(applicationName: String, webappConfDirectory: String): Configuration = {
    (new GuardianConfigurationStrategy).getConfiguration(applicationName, webappConfDirectory)
  }
}