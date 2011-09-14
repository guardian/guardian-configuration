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

import com.gu.conf.fixtures.PropertiesBuilder
import org.scalatest.BeforeAndAfter

class PropertiesBasedConfigurationTest extends AbstractConfigurationTestBase with BeforeAndAfter {

  before {
    val properties = Map(
      "precendence.test.property" -> "first",
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
      "akey" -> "abc123")

    val builder = new PropertiesBuilder
    properties foreach {
      case (key, value) =>
        builder.property(key, value)
    }

    configuration = new PropertiesBasedConfiguration("properties", builder.toProperties)
  }

  test("should get property source") {
    configuration.getPropertySource("nonnumeric.property").get should be(configuration)
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