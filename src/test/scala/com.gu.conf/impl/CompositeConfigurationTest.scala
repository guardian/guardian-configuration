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

class CompositeConfigurationTest extends AbstractConfigurationTestBase with BeforeAndAfter {

  var secondary: Configuration = _

  before {
    val conf1 = new MapBasedConfiguration("primary", Map(
      "precendence.test.property" -> "first",
      "double.property" -> "25.0"))

    val conf2 = new MapBasedConfiguration("secondary", Map(
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
      "akey" -> "abc123"))

    configuration = new CompositeConfiguration(conf1, conf2)
    secondary = conf2
  }

  test("should get property source") {
    configuration.getPropertySource("nonnumeric.property").get should be(secondary)
  }

  test("should give precedence to first configuration") {
    configuration("precendence.test.property") should be("first")
  }

  test("should toString() in standard format") {
    configuration.toString() should be(
      "# Properties from primary\n" +
        "double.property=25.0\n" +
        "precendence.test.property=first\n" +
        "\n" +
        "# Properties from secondary\n" +
        "akey=abc123\n" +
        "blah.pass.foo=*** PASSWORD ***\n" +
        "foo.key.blah=*** KEY ***\n" +
        "foo.password.blah=*** PASSWORD ***\n" +
        "integer.property=23\n" +
        "key=*** KEY ***\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "password=*** PASSWORD ***\n" +
        "utility.property=utility\n" +
        "\n")
  }

  test("should factory composite configurations") {
    val conf1 = new MapBasedConfiguration("conf1", Map(
      "conf1.property" -> "conf1"))

    val conf2 = new MapBasedConfiguration("conf2", Map(
      "conf1.property" -> "conf2",
      "conf2.property" -> "conf2"))

    val conf3 = new MapBasedConfiguration("conf3", Map(
      "conf1.property" -> "conf3",
      "conf2.property" -> "conf3",
      "conf3.property" -> "conf3"))

    val conf4 = new MapBasedConfiguration("conf4", Map(
      "conf1.property" -> "conf4",
      "conf2.property" -> "conf4",
      "conf3.property" -> "conf4",
      "conf4.property" -> "conf4"))

    val configuration = CompositeConfiguration.from(conf1, conf2, conf3, conf4)

    configuration.size should be(4)
    configuration("conf1.property") should be("conf1")
    configuration.getPropertySource("conf1.property").get should be(conf1)
    configuration("conf2.property") should be("conf2")
    configuration.getPropertySource("conf2.property").get should be(conf2)
    configuration("conf3.property") should be("conf3")
    configuration.getPropertySource("conf3.property").get should be(conf3)
    configuration("conf4.property") should be("conf4")
    configuration.getPropertySource("conf4.property").get should be(conf4)
  }
}