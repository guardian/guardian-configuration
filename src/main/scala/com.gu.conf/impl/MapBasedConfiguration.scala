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

private[conf] class MapBasedConfiguration(
    val identifier: String,
    val properties: Map[String, String] = Map()) extends AbstractConfiguration {

  def getIdentifier = identifier

  def getPropertySource(propertyName: String): Option[AbstractConfiguration] = {
    (properties contains propertyName) match {
      case true => Some(this)
      case _ => None
    }
  }

  def getStringProperty(propertyName: String): Option[String] = properties get propertyName

  def getPropertyNames: Set[String] = properties.keySet.toSet

}