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

import java.util.regex.Pattern

private[conf] class PlaceholderProcessingConfiguration(
  val delegate: AbstractConfiguration,
  private val placeholderResolver: PlaceholderResolver = new PlaceholderResolver) extends AbstractConfiguration {

  def getIdentifier: String = delegate.getIdentifier

  def getPropertySource(propertyName: String): Option[AbstractConfiguration] = {
    delegate getPropertySource propertyName
  }

  def getStringProperty(propertyName: String): Option[String] = {
    (delegate getStringProperty propertyName) map { placeholderResolver.substitutePlaceholders }
  }

  def getPropertyNames: Set[String] = delegate.getPropertyNames

  override def toString(): String = placeholderResolver.substitutePlaceholders(delegate.toString())

}

private[conf] class PlaceholderResolver(
  val environment: SystemEnvironmentConfiguration = new SystemEnvironmentConfiguration,
  val system: SystemPropertiesConfiguration = new SystemPropertiesConfiguration) {

  private val PLACEHOLDER: Pattern = Pattern.compile("\\$\\{(.*?)\\}")
  private final val PLACEHOLDER_NAME: Pattern = Pattern.compile("(env\\.)?(.*)")

  def substitutePlaceholders(text: String): String = {
    val substituted: StringBuffer = new StringBuffer

    val matcher = PLACEHOLDER.matcher(text)
    while (matcher.find) {
      matcher.appendReplacement(substituted, resolvePlaceholder(matcher.group(1)))
    }
    matcher.appendTail(substituted)

    substituted.toString
  }

  private def resolvePlaceholder(placeholder: String): String = {
    val matcher = PLACEHOLDER_NAME.matcher(placeholder)
    matcher.matches

    val property: String = matcher.group(2)
    val substitutionType = Option(matcher.group(1)).isDefined match {
      case true => environment
      case _ => system
    }

    substitutionType.getStringProperty(property) getOrElse ("${%s}" format placeholder)
  }
}