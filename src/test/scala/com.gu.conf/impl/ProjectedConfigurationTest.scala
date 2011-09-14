
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
    val mapBasedConfiguration = new MapBasedConfiguration("test", Map(
      "double.property" -> "25.0",
      "precendence.test.property" -> "second",
      "integer.property" -> "23",
      "nonnumeric.property" -> "qwe",
      "list.property" -> "rimbaud,verlaine",
      "utility.property" -> "utility",
      "password" -> "abc123",
      "foo.password.blah" -> "abc123",
      "blah.pass.foo" -> "abc123",
      "key" -> "abc123",
      "foo.key.blah" -> "abc123",
      "akey" -> "abc123",
      "projected.out" -> "lost"))

    val projectionNames = mapBasedConfiguration.getPropertyNames -- Set("projected.out")

    configuration = new ProjectedConfiguration(mapBasedConfiguration, projectionNames)
    original = mapBasedConfiguration
  }

  test("should contain projected properties") {
    configuration.size should be(12)

    val expected = original.getPropertyNames -- Set("projected.out")
    expected foreach { name =>
      configuration.hasProperty(name) should be(true)
    }
  }

  test("should not contain projected out properties") {
    configuration.hasProperty("projected.out") should be(false)
  }

  test("should have same property values as original") {
    configuration.getPropertyNames foreach { name =>
      configuration(name) should be(original(name))
    }
  }

  test("should get property source") {
    configuration.getPropertySource("nonnumeric.property").get should be(original)
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
        "utility.property=utility\n" +
        "\n")
  }
}