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

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

trait AbstractConfigurationTestBase extends FunSuite with ShouldMatchers {

  var configuration: AbstractConfiguration = _

  test("should have correct size") {
    configuration.size() should be(6)
  }

  test("should have correct test data") {
    configuration.hasProperty("precendence.test.property") should be(true)
    configuration.hasProperty("utility.property") should be(true)
    configuration("double.property") should be("25.0")
    configuration("integer.property") should be("23")
    configuration("nonnumeric.property") should be("qwe")
    configuration("list.property") should be("rimbaud,verlaine")
  }

  test("should get none for getPropertySource if not set") {
    configuration.getPropertySource("nosuch.property") should be(None)
  }

  test("should get false for hasProperty if not set") {
    configuration.hasProperty("nosuch.property") should be(false)
  }

  test("should get true for hasProperty if set") {
    configuration.hasProperty("nonnumeric.property") should be(true)
  }

  test("should get property value") {
    configuration("nonnumeric.property") should be("qwe")
  }

  test("should return none for property if not set") {
    configuration.getStringProperty("nosuch.property") should be(None)
  }

  test("should get default for property if not set") {
    configuration.getStringProperty("nosuch.property", "default") should be("default")
  }

  test("should get integer property") {
    configuration.getIntegerProperty("integer.property").get should be(23)
  }

  test("should return none for integer property if not set") {
    configuration.getIntegerProperty("nosuch.property") should be(None)
  }

  test("should throw for integer property if not integer") {
    evaluating {
      configuration.getIntegerProperty("double.property")
    } should produce[NumberFormatException]

    evaluating {
      configuration.getIntegerProperty("nonnumeric.property")
    } should produce[NumberFormatException]
  }

  test("should get default for integer property if not set") {
    configuration.getIntegerProperty("nosuch.property", 34) should be(34)
  }

  test("should get default for integer property if not integer") {
    configuration.getIntegerProperty("double.property", 45) should be(45)
    configuration.getIntegerProperty("nonnumeric.property", 65) should be(65)
  }

  test("should get property list") {
    val properties = configuration.getStringPropertiesSplitByComma("list.property")

    properties.size should be(2)
    properties should contain("rimbaud")
    properties should contain("verlaine")
  }

  test("should get property names") {
    val names = configuration.getPropertyNames

    names.size should be(6)
    names should contain("precendence.test.property")
    names should contain("double.property")
    names should contain("integer.property")
    names should contain("nonnumeric.property")
    names should contain("list.property")
    names should contain("utility.property")
  }

  test("should toProperties() with same properties") {
    val properties = configuration.toProperties

    properties.size should be(configuration.size())
    configuration.getPropertyNames foreach { property =>
      properties.getProperty(property) should be(configuration(property))
    }

  }

  test("should project correctly") {
    val projectionNames = Set(
      "precendence.test.property",
      "double.property",
      "integer.property",
      "nonnumeric.property",
      "utility.property")
    val projection = configuration.project(projectionNames)

    projection.size should be(5)
    projection.hasProperty("precendence.test.property") should be(true)
    projection.hasProperty("double.property") should be(true)
    projection.hasProperty("integer.property") should be(true)
    projection.hasProperty("nonnumeric.property") should be(true)
    projection.hasProperty("utility.property") should be(true)

    projection.getPropertyNames foreach { property =>
      projection(property) should be(configuration(property))
    }
  }

  test("should minus correctly") {
    val minusNames = Set("precendence.test.property")
    val projection = configuration.minus(minusNames)

    projection.size should be(5)
    projection.hasProperty("double.property") should be(true)
    projection.hasProperty("integer.property") should be(true)
    projection.hasProperty("nonnumeric.property") should be(true)
    projection.hasProperty("list.property") should be(true)
    projection.hasProperty("utility.property") should be(true)

    projection.getPropertyNames foreach { property =>
      projection(property) should be(configuration(property))
    }
  }

  test("should override") {
    val overrides = new MapBasedConfiguration("overrides")
    overrides.add("utility.property", "overriden")

    val withOverrides = configuration.overrideWith(overrides)

    withOverrides.size should be(6)
    withOverrides.hasProperty("double.property") should be(true)
    withOverrides.hasProperty("precendence.test.property") should be(true)
    withOverrides.hasProperty("integer.property") should be(true)
    withOverrides.hasProperty("nonnumeric.property") should be(true)
    withOverrides.hasProperty("list.property") should be(true)
    withOverrides.hasProperty("utility.property") should be(true)

    withOverrides("utility.property") should be("overriden")
    val unchanged = withOverrides.getPropertyNames -- Set("utility.property")
    unchanged foreach { property =>
      withOverrides(property) should be(configuration(property))
    }
  }

}