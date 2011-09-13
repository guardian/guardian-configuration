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
      "double.property" -> "25.0",
      "precendence.test.property" -> "second",
      "integer.property" -> "23",
      "nonnumeric.property" -> "qwe",
      "list.property" -> "rimbaud,verlaine",
      "utility.property" -> "has a ${placeholder}"))

    val placeholderResolver = new PlaceholderResolver(
      new SystemEnvironmentConfiguration(properties = Map()),
      new SystemPropertiesConfiguration(properties = new PropertiesBuilder().
        property("placeholder", "resolved placeholder").
        toProperties))

    configuration = new PlaceholderProcessingConfiguration(mapBasedConfiguration, placeholderResolver)
  }

  test("should replace placeholder with resolved value") {
    configuration.hasProperty("utility.property") should be(true)
    configuration("utility.property") should be("has a resolved placeholder")
  }

  test("should toString() in standard format") {
    configuration.toString() should be(
      "# Properties from " + configuration.getIdentifier + "\n" +
        "double.property=25.0\n" +
        "integer.property=23\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "precendence.test.property=second\n" +
        "utility.property=has a resolved placeholder\n" +
        "\n")
  }
}