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
import org.junit.Before
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is

class PropertiesFileBasedConfigurationTest extends AbstractConfigurationTestBase {

  @Before
  def setUp() {
    val builder = new PropertiesBuilder
    builder.property("precendence.test.property", "first")
    builder.property("double.property", "25.0")
    builder.property("integer.property", "23")
    builder.property("nonnumeric.property", "qwe")
    builder.property("list.property", "rimbaud,verlaine")
    builder.property("utility.property", "utility")

    configuration = new PropertiesFileBasedConfiguration("properties", builder.toProperties)
  }

  @Test
  def shouldGetPropertySource() {
    assertThat(configuration.getPropertySource("nonnumeric.property").get, is(configuration.asInstanceOf[Object]))
  }

  @Test
  def shouldToStringInStandardFormat() {
    val expected =
      "# Properties from " + configuration.getIdentifier + "\n" +
        "double.property=25.0\n" +
        "integer.property=23\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "precendence.test.property=first\n" +
        "utility.property=utility\n" +
        "\n"

    assertThat(configuration.toString(), is(expected))
  }
}