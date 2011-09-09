
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
import org.junit.Before
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is

class ProjectedConfigurationTest extends AbstractConfigurationTestBase {
  var original: Configuration = null

  @Before
  def setUp() {
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

  @Test
  def shouldContainedProjectedProperties() {
    assertThat(configuration.size(), is(6))
    assertThat(configuration.hasProperty("precendence.test.property"), is(true))
    assertThat(configuration.hasProperty("double.property"), is(true))
    assertThat(configuration.hasProperty("integer.property"), is(true))
    assertThat(configuration.hasProperty("nonnumeric.property"), is(true))
    assertThat(configuration.hasProperty("list.property"), is(true))
    assertThat(configuration.hasProperty("utility.property"), is(true))
  }

  @Test
  def shouldNotContainedProjectedOutProperties() {
    assertThat(configuration.hasProperty("projected.out"), is(false))
  }

  @Test
  def shouldHaveSamePropertyValuesAsOriginal() {
    assertThat(configuration.getStringProperty("precendence.test.property").get, is("second".asInstanceOf[Object]))
    assertThat(configuration.getStringProperty("double.property").get, is("25.0".asInstanceOf[Object]))
    assertThat(configuration.getStringProperty("integer.property").get, is("23".asInstanceOf[Object]))
    assertThat(configuration.getStringProperty("nonnumeric.property").get, is("qwe".asInstanceOf[Object]))
    assertThat(configuration.getStringProperty("list.property").get, is("rimbaud,verlaine".asInstanceOf[Object]))
  }

  @Test
  def shouldGetPropertySource() {
    assertThat(configuration.getPropertySource("nonnumeric.property").get, is(original.asInstanceOf[Object]))
  }

  @Test
  def shouldToStringInStandardFormat() {
    val expected =
      "# Properties from " + configuration.getIdentifier + "\n" +
        "double.property=25.0\n" +
        "integer.property=23\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "precendence.test.property=second\n" +
        "utility.property=utility\n" +
        "\n"

    assertThat(configuration.toString(), is(expected))
  }
}