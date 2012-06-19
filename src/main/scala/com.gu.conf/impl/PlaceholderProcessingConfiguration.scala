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

import scala.util.matching.Regex.Match

private[conf] class PlaceholderProcessingConfiguration(
    val delegate: AbstractConfiguration,
    private val environment: SystemEnvironmentConfiguration = new SystemEnvironmentConfiguration,
    private val system: SystemPropertiesConfiguration = new SystemPropertiesConfiguration) extends AbstractConfiguration {

  private val PLACEHOLDER = """\$\{(env\.)?(.+)\}""".r

  private def substitutePlaceholders(text: String): String = {
    PLACEHOLDER replaceSomeIn (text, (m: Match) => {
      val property = m.group(2)
      val substitution = Option(m.group(1)) match {
        case None => system
        case _ => environment
      }

      substitution getStringProperty property
    })
  }

  def getIdentifier: String = delegate.getIdentifier

  def getPropertySource(propertyName: String): Option[AbstractConfiguration] = {
    delegate getPropertySource propertyName
  }

  def getStringProperty(propertyName: String): Option[String] = {
    (delegate getStringProperty propertyName) map { substitutePlaceholders }
  }

  def getPropertyNames: Set[String] = delegate.getPropertyNames

  override def toString: String = substitutePlaceholders(delegate.toString())

}
