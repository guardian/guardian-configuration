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

private[conf] object CompositeConfiguration {
  def from(conf: AbstractConfiguration, confs: AbstractConfiguration*): AbstractConfiguration = {
    confs.toList match {
      case Nil => conf
      case head :: tail => new CompositeConfiguration(conf, from(head, tail: _*))
    }
  }
}

private[conf] class CompositeConfiguration(
    val primary: AbstractConfiguration,
    val secondary: AbstractConfiguration) extends AbstractConfiguration {

  def getIdentifier = "composite[%s//%s]".format(primary.getIdentifier, secondary.getIdentifier)

  def getPropertySource(propertyName: String): Option[AbstractConfiguration] = {
    (primary getPropertySource propertyName) orElse (secondary getPropertySource propertyName)
  }

  def getStringProperty(propertyName: String): Option[String] = {
    (primary getStringProperty propertyName) orElse (secondary getStringProperty propertyName)
  }

  def getPropertyNames: Set[String] = primary.getPropertyNames ++ secondary.getPropertyNames

  override def toString(): String = {
    val stringBuilder = new StringBuilder
    stringBuilder.append(primary.toString())
    stringBuilder.append(secondary.minus(primary).toString())

    stringBuilder.toString()
  }
}