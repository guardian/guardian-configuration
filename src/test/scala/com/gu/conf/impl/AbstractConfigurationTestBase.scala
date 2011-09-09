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

import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is
import org.junit.Assert.fail

trait AbstractConfigurationTestBase {

  var configuration: AbstractConfiguration = _

  @Test
  def shouldHaveCorrectSize() {
    assertThat(configuration.size(), is(6))
  }

  @Test
  def shouldHaveCorrectTestData() {
    assertThat(configuration.hasProperty("precendence.test.property"), is(true))
    assertThat(configuration.hasProperty("utility.property"), is(true))
    assertThat(configuration.getStringProperty("double.property").get, is("25.0"))
    assertThat(configuration.getStringProperty("integer.property").get, is("23"))
    assertThat(configuration.getStringProperty("nonnumeric.property").get, is("qwe"))
    assertThat(configuration.getStringProperty("list.property").get, is("rimbaud,verlaine"))
  }

  @Test
  def shouldGetNullForPropertySourceIfNotSet() {
    assertThat(configuration.getPropertySource("nosuch.property"), is(None.asInstanceOf[Object]))
  }

  @Test
  def shouldGetFalseForHasPropertyIfNotSet() {
    assertThat(configuration.hasProperty("nosuch.property"), is(false))
  }

  @Test
  def shouldGetTrueForHasPropertyIfSet() {
    assertThat(configuration.hasProperty("nonnumeric.property"), is(true))
  }

  @Test
  def shouldGetProperty() {
    assertThat(configuration.getStringProperty("nonnumeric.property").get, is("qwe"))
  }

  @Test
  def shouldReturnNoneForPropertyIfNotSet() {
    assertThat(configuration.getStringProperty("nosuch.property"), is(None.asInstanceOf[Object]))
  }

  @Test
  def shouldGetDefaultForPropertyIfNotSet() {
    assertThat(configuration.getStringProperty("nosuch.property", "default"), is("default"))
  }

  @Test
  def shouldGetIntegerProperty() {
    assertThat(configuration.getIntegerProperty("integer.property").get, is(23))
  }

  @Test
  def shouldReturnNoneForIntegerPropertyIfNotSet() {
    assertThat(configuration.getIntegerProperty("nosuch.property"), is(None.asInstanceOf[Object]))
  }

  @Test
  def shouldThrowForIntegerPropertyIfNotInteger() {
    try {
      configuration.getIntegerProperty("double.property")
      fail("exception expected")
    } catch {
      case _: NumberFormatException =>
    }

    try {
      configuration.getIntegerProperty("nonnumeric.property")
      fail("exception expected")
    } catch {
      case _: NumberFormatException =>
    }
  }

  @Test
  def shouldGetDefaultForIntegerPropertyIfNotSet() {
    assertThat(configuration.getIntegerProperty("nosuch.property", 34), is(34))
  }

  @Test
  def shouldGetDefaultForIntegerPropertyIfNotInteger() {
    assertThat(configuration.getIntegerProperty("double.property", 45), is(45))
    assertThat(configuration.getIntegerProperty("nonnumeric.property", 65), is(65))
  }

  @Test
  def shouldGetPropertyList() {
    val properties = configuration.getStringPropertiesSplitByComma("list.property")

    assertThat(properties.size, is(2))
    assertThat(properties(0), is("rimbaud"))
    assertThat(properties(1), is("verlaine"))
  }

  @Test
  def shouldGetPropertyNames() {
    val names = configuration.getPropertyNames

    assertThat(names.size, is(6))
    assertThat(names.contains("precendence.test.property"), is(true))
    assertThat(names.contains("double.property"), is(true))
    assertThat(names.contains("integer.property"), is(true))
    assertThat(names.contains("nonnumeric.property"), is(true))
    assertThat(names.contains("list.property"), is(true))
    assertThat(names.contains("utility.property"), is(true))
  }

  @Test
  def shouldToPropertiesWithSameProperties() {
    val properties = configuration.toProperties

    assertThat(properties.size, is(configuration.size()))
    configuration.getPropertyNames foreach { property =>
      assertThat(properties.getProperty(property), is(configuration.getStringProperty(property).get))
    }

  }

  @Test
  def shouldProjectCorrectly() {
    val projectionNames = Set(
      "precendence.test.property",
      "double.property",
      "integer.property",
      "nonnumeric.property",
      "utility.property")
    val projection = configuration.project(projectionNames)

    assertThat(projection.size(), is(5))
    assertThat(projection.hasProperty("precendence.test.property"), is(true))
    assertThat(projection.hasProperty("double.property"), is(true))
    assertThat(projection.hasProperty("integer.property"), is(true))
    assertThat(projection.hasProperty("nonnumeric.property"), is(true))
    assertThat(projection.hasProperty("utility.property"), is(true))
    projection.getPropertyNames foreach { property =>
      assertThat(projection.getStringProperty(property).get, is(configuration.getStringProperty(property).get))
    }
  }

  @Test
  def shouldMinusCorrectly() {
    val minusNames = Set("precendence.test.property")
    val projection = configuration.minus(minusNames)

    assertThat(projection.size(), is(5))
    assertThat(projection.hasProperty("double.property"), is(true))
    assertThat(projection.hasProperty("integer.property"), is(true))
    assertThat(projection.hasProperty("nonnumeric.property"), is(true))
    assertThat(projection.hasProperty("list.property"), is(true))
    assertThat(projection.hasProperty("utility.property"), is(true))
    projection.getPropertyNames foreach { property =>
      assertThat(projection.getStringProperty(property).get, is(configuration.getStringProperty(property).get))
    }
  }

  @Test
  def shouldOverride() {
    val overrides = new MapBasedConfiguration("overrides")
    overrides.add("utility.property", "overriden")

    val withOverrides = configuration.overrideWith(overrides)

    assertThat(withOverrides.size(), is(6))
    assertThat(withOverrides.hasProperty("double.property"), is(true))
    assertThat(withOverrides.hasProperty("precendence.test.property"), is(true))
    assertThat(withOverrides.hasProperty("integer.property"), is(true))
    assertThat(withOverrides.hasProperty("nonnumeric.property"), is(true))
    assertThat(withOverrides.hasProperty("list.property"), is(true))
    assertThat(withOverrides.hasProperty("utility.property"), is(true))
    assertThat(withOverrides.getStringProperty("utility.property").get, is("overriden"))
    val unchanged = withOverrides.getPropertyNames -- Set("utility.property")
    unchanged foreach { property =>
      assertThat(withOverrides.getStringProperty(property), is(configuration.getStringProperty(property)))
    }
  }

}