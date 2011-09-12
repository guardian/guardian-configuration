
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

import com.gu.conf.Configuration
import org.scalatest.BeforeAndAfter

class ProjectedConfigurationTest extends AbstractConfigurationTestBase with BeforeAndAfter {
  var original: Configuration = _

  before {
    val mapBasedConfiguration = new MapBasedConfiguration("test")
    mapBasedConfiguration.add("double.property", "25.0")
    mapBasedConfiguration.add("precendence.test.property", "second")
    mapBasedConfiguration.add("integer.property", "23")
    mapBasedConfiguration.add("nonnumeric.property", "qwe")
    mapBasedConfiguration.add("list.property", "rimbaud,verlaine")
    mapBasedConfiguration.add("utility.property", "utility")
    mapBasedConfiguration.add("projected.out", "lost")

    val projectionNames = Set(
      "precendence.test.property",
      "double.property",
      "integer.property",
      "nonnumeric.property",
      "list.property",
      "utility.property")

    configuration = new ProjectedConfiguration(mapBasedConfiguration, projectionNames)
    original = mapBasedConfiguration
  }

  test("should contain projected properties") {
    configuration.size() should be(6)
    configuration.hasProperty("precendence.test.property") should be(true)
    configuration.hasProperty("double.property") should be(true)
    configuration.hasProperty("integer.property") should be(true)
    configuration.hasProperty("nonnumeric.property") should be(true)
    configuration.hasProperty("list.property") should be(true)
    configuration.hasProperty("utility.property") should be(true)
  }

  test("should not contain projected out properties") {
    configuration.hasProperty("projected.out") should be(false)
  }

  test("should have same property values as original") {
    configuration("precendence.test.property") should be("second")
    configuration("double.property") should be("25.0")
    configuration("integer.property") should be("23")
    configuration("nonnumeric.property") should be("qwe")
    configuration("list.property") should be("rimbaud,verlaine")
  }

  test("should get property source") {
    configuration.getPropertySource("nonnumeric.property").get should be(original)
  }

  test("should toString() in standard format") {
    configuration.toString() should be(
      "# Properties from " + configuration.getIdentifier + "\n" +
        "double.property=25.0\n" +
        "integer.property=23\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "precendence.test.property=second\n" +
        "utility.property=utility\n" +
        "\n")
  }
}