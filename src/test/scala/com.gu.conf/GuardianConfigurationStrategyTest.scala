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
package com.gu.conf

import com.gu.conf.fixtures.PropertiesBuilder
import com.gu.conf.impl._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, BeforeAndAfter, FunSuite}

class GuardianConfigurationStrategyTest extends FunSuite with Matchers with MockitoSugar with BeforeAndAfter {

  val SETUP_PROPERTIES = "file:/etc/gu/setup.properties"

  val DEVELOPER_ACCOUNT_OVERRIDE_PROPERTIES = "file://%s/.gu/webapp.properties" format System.getProperty("user.home")
  val OPERATIONS_PROPERTIES = "file:///etc/gu/webapp.properties"
  val DEVELOPER_STAGE_BASED_PROPERTIES = "classpath:conf/DEV.properties"
  val DEVELOPER_SERVICE_DOMAIN_BASED_PROPERTIES = "classpath:conf/gudev.gnl.properties"
  val DEVELOPER_COMMON_PROPERTIES = "classpath:conf/global.properties"

  var loader = mock[PropertiesLoader]
  var setup = mock[SetupConfiguration]

  var strategy: GuardianConfigurationStrategy = _

  before {
    when(setup.getServiceDomain).thenReturn("gudev.gnl")
    when(setup.getStage).thenReturn("DEV")
    when(setup.getEnvironmentVariables).thenReturn(Map("foo" -> "bar"))

    when(loader.getPropertiesFrom(DEVELOPER_ACCOUNT_OVERRIDE_PROPERTIES)).
      thenReturn(new PropertiesBuilder().
        property("developer.account.override.properties", "available").
        property("source", "developer.account.override.properties").
        toProperties)

    when(loader.getPropertiesFrom(OPERATIONS_PROPERTIES)).
      thenReturn(new PropertiesBuilder().
        property("operations.properties", "available").
        property("source", "operations.properties").
        toProperties)

    when(loader.getPropertiesFrom(DEVELOPER_STAGE_BASED_PROPERTIES)).
      thenReturn(new PropertiesBuilder().
        property("developer.stage.based.properties", "available").
        property("source", "developer.stage.based.properties").
        property("developer.stage.based.properties.precendence", "developer.stage.based.properties").
        toProperties)

    when(loader.getPropertiesFrom(DEVELOPER_SERVICE_DOMAIN_BASED_PROPERTIES)).
      thenReturn(new PropertiesBuilder().
        property("developer.service.domain.properties", "available").
        property("source", "developer.service.domain.properties").
        property("developer.stage.based.properties.precendence", "developer.service.domain.properties").
        toProperties)

    when(loader.getPropertiesFrom(DEVELOPER_COMMON_PROPERTIES)).
      thenReturn(new PropertiesBuilder().
        property("developer.common.properties", "available").
        property("source", "developer.common.properties").
        toProperties)

    when(loader.getPropertiesFrom(SETUP_PROPERTIES)).
      thenReturn(new PropertiesBuilder().
        property("setup.properties", "available").
        property("source", "setup.properties").
        property("int.service.domain", "gudev.gnl").
        property("stage", "DEV").
        toProperties)

    strategy = new GuardianConfigurationStrategy(loader, setup)
  }

  test("should check setup properties are not available to applications") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration should not be (null)
    configuration.hasProperty("setup.properties") should be(false)
    configuration("source") should not be ("setup.properties")
    configuration.hasProperty("int.service.domain") should be(false)
    configuration.hasProperty("stage") should be(false)
  }

  test("should not override properties with system properties if defined") {
    try {
      System.setProperty("source", "system.override.property")
      System.setProperty("random.system.property", "meh")

      val configuration = strategy.getConfiguration("webapp", "conf")

      configuration("source") should be("developer.account.override.properties")
      configuration.hasProperty("random.system.property") should be(false)
    } finally {
      System.clearProperty("source")
      System.clearProperty("random.system.property")
    }
  }

  test("should load developer account override properties") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration should not be (null)
    configuration("developer.account.override.properties") should be("available")
    configuration.hasProperty("no-property") should be(false)
  }

  test("should load operations properties") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration should not be (null)
    configuration("operations.properties") should be("available")
    configuration.hasProperty("no-property") should be(false)
  }

  test("should load developer properties(stage based)") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration should not be (null)
    configuration("developer.stage.based.properties") should be("available")
    configuration.hasProperty("no-property") should be(false)
  }

  test("should load developer properties(service domain based)") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration should not be (null)
    configuration("developer.service.domain.properties") should be("available")
    configuration.hasProperty("no-property") should be(false)
  }

  test("should load developer common properties") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration should not be (null)
    configuration("developer.common.properties") should be("available")
    configuration.hasProperty("no-property") should be(false)
  }

  test("should provide developer stage based properties in preference to service domain based properties") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration("developer.stage.based.properties.precendence") should be("developer.stage.based.properties")
  }

  test("should provide environment variables") {
    val configuration = strategy.getConfiguration("webapp", "conf")
    configuration.hasProperty("foo") should be(true)
  }

  test("should have pretty toString()") {
    val system = System.getProperties
    System.setProperties(new PropertiesBuilder().
      property("user.home", system.getProperty("user.home")).
      toProperties)

    val configuration = strategy.getConfiguration("webapp", "conf")

    configuration.toString.contains(
      "# Properties from file://" + System.getProperty("user.home") + "/.gu/webapp.properties\n" +
        "developer.account.override.properties=available\n" +
        "source=developer.account.override.properties\n" +
        "\n" +
        "# Properties from file:///etc/gu/webapp.properties\n" +
        "operations.properties=available\n" +
        "\n" +
        "# Properties from classpath:conf/DEV.properties\n" +
        "developer.stage.based.properties=available\n" +
        "developer.stage.based.properties.precendence=developer.stage.based.properties\n" +
        "\n" +
        "# Properties from classpath:conf/gudev.gnl.properties\n" +
        "developer.service.domain.properties=available\n" +
        "\n" +
        "# Properties from classpath:conf/global.properties\n" +
        "developer.common.properties=available\n" +
        "\n") should be(true)
    configuration.toString.contains("# Properties from Environment") should be(true)
    configuration.toString.contains("foo=bar\n") should be(true)

    System.setProperties(system)
  }

}