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

class MapBasedConfigurationTest extends AbstractConfigurationTestBase with BeforeAndAfter {

  before {
    val mapBasedConfiguration = new MapBasedConfiguration("test")
    mapBasedConfiguration.add("precendence.test.property", "first")
    mapBasedConfiguration.add("double.property", "25.0")
    mapBasedConfiguration.add("precendence.test.property", "second")
    mapBasedConfiguration.add("integer.property", "23")
    mapBasedConfiguration.add("nonnumeric.property", "qwe")
    mapBasedConfiguration.add("list.property", "rimbaud,verlaine")
    mapBasedConfiguration.add("utility.property", "utility")

    configuration = mapBasedConfiguration
  }

  test("should get property source") {
    configuration.getPropertySource("nonnumeric.property").get should be(configuration)
  }

  test("should not respect first declaration precedence in get property") {
    configuration("precendence.test.property") should be("second")
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