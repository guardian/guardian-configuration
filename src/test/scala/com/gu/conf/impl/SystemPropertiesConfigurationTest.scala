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

import org.junit.After
import org.junit.Before

class SystemPropertiesConfigurationTest extends AbstractConfigurationTestBase {
  @Before
  def setUp() {
    System.setProperty("double.property", "25.0")
    System.setProperty("precendence.test.property", "second")
    System.setProperty("integer.property", "23")
    System.setProperty("nonnumeric.property", "qwe")
    System.setProperty("list.property", "rimbaud,verlaine")
    System.setProperty("utility.property", "utility")

    val projectionNames = Set(
      "precendence.test.property",
      "double.property",
      "integer.property",
      "nonnumeric.property",
      "list.property",
      "utility.property")
    configuration = new ProjectedConfiguration(new SystemPropertiesConfiguration, projectionNames)
  }

  @After
  def tearDown() {
    configuration.getPropertyNames foreach { property =>
      System.clearProperty(property)
    }
  }
}