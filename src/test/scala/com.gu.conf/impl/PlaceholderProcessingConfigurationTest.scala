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

import org.scalatest.BeforeAndAfter
import com.gu.conf.fixtures.PropertiesBuilder

class PlaceholderProcessingConfigurationTest extends AbstractConfigurationTestBase with BeforeAndAfter {

  before {
    val mapBasedConfiguration = new MapBasedConfiguration("test", Map(
      "precendence.test.property" -> "first",
      "double.property" -> "25.0",
      "precendence.test.property" -> "second",
      "integer.property" -> "23",
      "nonnumeric.property" -> "qwe",
      "list.property" -> "rimbaud,verlaine",
      "password" -> "abc123",
      "foo.password.blah" -> "abc123",
      "blah.pass.foo" -> "abc123",
      "key" -> "abc123",
      "foo.key.blah" -> "abc123",
      "akey" -> "abc123",
      "utility.property" -> "has a ${placeholder}"))

    configuration = new PlaceholderProcessingConfiguration(mapBasedConfiguration,
      new SystemEnvironmentConfiguration("TestEnvironment", Map()),
      new SystemPropertiesConfiguration("TestSystem", new PropertiesBuilder().
        property("placeholder", "resolved system placeholder").
        toProperties))
  }

  test("should replace placeholder with resolved system value") {
    val mapBasedConfiguration = new MapBasedConfiguration("test", Map(
      "property" -> "has a ${placeholder}"))
    val environment = new SystemEnvironmentConfiguration("TestEnvironment", Map())
    val system = new SystemPropertiesConfiguration("TestSystem", new PropertiesBuilder().
      property("placeholder", "resolved system placeholder").
      toProperties)

    configuration = new PlaceholderProcessingConfiguration(mapBasedConfiguration, environment, system)

    configuration.hasProperty("property") should be(true)
    configuration("property") should be("has a resolved system placeholder")
  }

  test("should replace placeholder with resolved environment value") {
    val mapBasedConfiguration = new MapBasedConfiguration("test", Map(
      "property" -> "has a ${env.placeholder}"))
    val environment = new SystemEnvironmentConfiguration("TestEnvironment", Map(
      "placeholder" -> "resolved environment placeholder"))
    val system = new SystemPropertiesConfiguration("TestSystem", new PropertiesBuilder().toProperties)

    configuration = new PlaceholderProcessingConfiguration(mapBasedConfiguration, environment, system)

    configuration.hasProperty("property") should be(true)
    configuration("property") should be("has a resolved environment placeholder")
  }

  test("should toString() in standard format") {
    configuration.toString() should be(
      "# Properties from " + configuration.getIdentifier + "\n" +
        "akey=abc123\n" +
        "blah.pass.foo=*** PASSWORD ***\n" +
        "double.property=25.0\n" +
        "foo.key.blah=*** KEY ***\n" +
        "foo.password.blah=*** PASSWORD ***\n" +
        "integer.property=23\n" +
        "key=*** KEY ***\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "password=*** PASSWORD ***\n" +
        "precendence.test.property=second\n" +
        "utility.property=has a resolved system placeholder\n" +
        "\n")
  }
}