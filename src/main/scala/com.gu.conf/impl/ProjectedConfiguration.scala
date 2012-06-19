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

private[conf] class ProjectedConfiguration(
    val delegate: AbstractConfiguration,
    val projection: Set[String]) extends AbstractConfiguration {

  private val propertyNames = projection.intersect(delegate.getPropertyNames)

  def getIdentifier: String = delegate.getIdentifier

  def getPropertySource(propertyName: String): Option[AbstractConfiguration] = {
    (propertyNames contains propertyName) match {
      case true => delegate.getPropertySource(propertyName)
      case false => None
    }
  }

  def getStringProperty(propertyName: String): Option[String] = {
    (propertyNames contains propertyName) match {
      case true => delegate.getStringProperty(propertyName)
      case false => None
    }
  }

  def getPropertyNames: Set[String] = propertyNames

  override def toString(): String = {
    var result = delegate.toString()
    (delegate.getPropertyNames -- getPropertyNames) foreach { remove =>
      result = result.replaceAll(remove + "=(.*)\n", "")
    }

    result
  }
}