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

import java.util.Collections
import org.junit.After
import org.junit.Before
import scala.collection.JavaConversions._

class SystemEnvironmentConfigurationTest extends AbstractConfigurationTestBase {

  @Before
  def setUp() {
    oldEnvironment = Map()
    System.getenv().keySet() foreach { key =>
      oldEnvironment = oldEnvironment + (key -> System.getenv(key))
    }

    var environment: Map[String, String] = Map()
    environment = environment + ("double.property" -> "25.0")
    environment = environment + ("precendence.test.property" -> "second")
    environment = environment + ("integer.property" -> "23")
    environment = environment + ("nonnumeric.property" -> "qwe")
    environment = environment + ("list.property" -> "rimbaud,verlaine")
    environment = environment + ("utility.property" -> "utility")

    setEnvironment(environment)

    configuration = new SystemEnvironmentConfiguration
  }

  private def setEnvironment(environment: Map[String, String])() {
    val actualEnv = System.getenv

    classOf[Collections].getDeclaredClasses filter {
      _.getName == "java.util.Collections$UnmodifiableMap"
    } foreach { clazz =>
      val backingCollectionfield = clazz.getDeclaredField("m")
      backingCollectionfield.setAccessible(true)

      val modifiableEnv = backingCollectionfield.get(actualEnv).asInstanceOf[java.util.Map[String, String]]
      modifiableEnv.clear()
      modifiableEnv.putAll(environment)
    }
  }

  @After
  def tearDown() {
    setEnvironment(oldEnvironment)
  }

  private var oldEnvironment: Map[String, String] = null
}