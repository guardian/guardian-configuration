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
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers._
import org.mockito.Mockito.when

@RunWith(classOf[MockitoJUnitRunner])
class GuardianConfigurationStrategyTest {

  val SETUP_PROPERTIES = "file:/etc/gu/setup.properties"

  val DEVELOPER_ACCOUNT_OVERRIDE_PROPERTIES = "file://%s/.gu/webapp.properties" format System.getProperty("user.home")
  val OPERATIONS_PROPERTIES = "file:///etc/gu/webapp.properties"
  val DEVELOPER_STAGE_BASED_PROPERTIES = "classpath:/conf/DEV.properties"
  val DEVELOPER_SERVICE_DOMAIN_BASED_PROPERTIES = "classpath:/conf/gudev.gnl.properties"
  val DEVELOPER_COMMON_PROPERTIES = "classpath:/conf/global.properties"

  @Mock
  var fileLoader: FileAndResourceLoader = _
  @Mock
  var setup: SetupConfiguration = _

  var strategy: GuardianConfigurationStrategy = _

  @Before
  def setUp() {
    when(setup.getServiceDomain).thenReturn("gudev.gnl")
    when(setup.getStage).thenReturn("DEV")

    var configuration = new ConfigurationBuilder().
      identifier(DEVELOPER_ACCOUNT_OVERRIDE_PROPERTIES).
      property("developer.account.override.properties", "available").
      property("source", "developer.account.override.properties").
      toConfiguration
    when(fileLoader.getConfigurationFrom(DEVELOPER_ACCOUNT_OVERRIDE_PROPERTIES)).thenReturn(configuration)

    configuration = new ConfigurationBuilder().
      identifier(OPERATIONS_PROPERTIES).
      property("operations.properties", "available").
      property("source", "operations.properties").
      toConfiguration
    when(fileLoader.getConfigurationFrom(OPERATIONS_PROPERTIES)).thenReturn(configuration)

    configuration = new ConfigurationBuilder().
      identifier(DEVELOPER_STAGE_BASED_PROPERTIES).
      property("developer.stage.based.properties", "available").
      property("source", "developer.stage.based.properties").
      property("developer.stage.based.properties.precendence", "developer.stage.based.properties").
      toConfiguration
    when(fileLoader.getConfigurationFrom(DEVELOPER_STAGE_BASED_PROPERTIES)).thenReturn(configuration)

    configuration = new ConfigurationBuilder().
      identifier(DEVELOPER_SERVICE_DOMAIN_BASED_PROPERTIES).
      property("developer.service.domain.properties", "available").
      property("source", "developer.service.domain.properties").
      property("developer.stage.based.properties.precendence", "developer.service.domain.properties").
      toConfiguration
    when(fileLoader.getConfigurationFrom(DEVELOPER_SERVICE_DOMAIN_BASED_PROPERTIES)).thenReturn(configuration)

    configuration = new ConfigurationBuilder().
      identifier(DEVELOPER_COMMON_PROPERTIES).
      property("developer.common.properties", "available").
      property("source", "developer.common.properties").
      toConfiguration
    when(fileLoader.getConfigurationFrom(DEVELOPER_COMMON_PROPERTIES)).thenReturn(configuration)

    configuration = new ConfigurationBuilder().
      identifier(SETUP_PROPERTIES).
      property("setup.properties", "available").
      property("source", "setup.properties").
      property("int.service.domain", "gudev.gnl").
      property("stage", "DEV").
      toConfiguration
    when(fileLoader.getConfigurationFrom(SETUP_PROPERTIES)).thenReturn(configuration)

    strategy = new GuardianConfigurationStrategy(fileLoader, setup)
  }

  @Test
  def shouldCheckSetupPropertiesAreNotAvailableToApplications() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration, notNullValue())
    assertThat(configuration.hasProperty("setup.properties"), is(false))
    assertThat(configuration.getStringProperty("source").get, not("setup.properties"))
    assertThat(configuration.hasProperty("int.service.domain"), is(false))
    assertThat(configuration.hasProperty("stage"), is(false))
  }

  @Test
  def shouldNotOverridePropertiesWithSystemPropertiesIfDefined() {
    try {
      System.setProperty("source", "system.override.property")
      System.setProperty("random.system.property", "meh")

      val configuration = strategy.getConfiguration("webapp", "/conf")

      assertThat(configuration.getStringProperty("source").get, is("developer.account.override.properties"))
      assertThat(configuration.hasProperty("random.system.property"), is(false))
    } finally {
      System.clearProperty("source")
      System.clearProperty("random.system.property")
    }
  }

  @Test
  def shouldLoadDeveloperAccountOverrideProperties() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration, notNullValue())
    assertThat(configuration.getStringProperty("developer.account.override.properties").get, is("available"))
    assertThat(configuration.hasProperty("no-property"), is(false))
  }

  @Test
  def shouldLoadOperationsProperties() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration, notNullValue())
    assertThat(configuration.getStringProperty("operations.properties").get, is("available"))
    assertThat(configuration.hasProperty("no-property"), is(false))
  }

  @Test
  def shouldLoadDeveloperStageBasedProperties() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration, notNullValue())
    assertThat(configuration.getStringProperty("developer.stage.based.properties").get, is("available"))
    assertThat(configuration.hasProperty("no-property"), is(false))
  }

  @Test
  def shouldLoadDeveloperServiceDomainBasedProperties() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration, notNullValue())
    assertThat(configuration.getStringProperty("developer.service.domain.properties").get, is("available"))
    assertThat(configuration.hasProperty("no-property"), is(false))
  }

  @Test
  def shouldLoadDeveloperCommonProperties() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration, notNullValue())
    assertThat(configuration.getStringProperty("developer.common.properties").get, is("available"))
    assertThat(configuration.hasProperty("no-property"), is(false))
  }

  @Test
  def shouldProvideDeveloperStageBasedPropertiesInPreferenceToServiceDomainBasedProperties() {
    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration.getStringProperty("developer.stage.based.properties.precendence").get,
      is("developer.stage.based.properties"))
  }

  @Test
  def shouldHavePrettyToString() {
    val system = System.getProperties
    System.setProperties(new PropertiesBuilder().
      property("user.home", system.getProperty("user.home")).
      toProperties)

    val configuration = strategy.getConfiguration("webapp", "/conf")
    assertThat(configuration.toString, is("# Properties from file://" + System.getProperty("user.home") + "/.gu/webapp.properties\n" + "developer.account.override.properties=available\n" + "source=developer.account.override.properties\n" + "\n" + "# Properties from file:///etc/gu/webapp.properties\n" + "operations.properties=available\n" + "\n" + "# Properties from classpath:/conf/DEV.properties\n" + "developer.stage.based.properties=available\n" + "developer.stage.based.properties.precendence=developer.stage.based.properties\n" + "\n" + "# Properties from classpath:/conf/gudev.gnl.properties\n" + "developer.service.domain.properties=available\n" + "\n" + "# Properties from classpath:/conf/global.properties\n" + "developer.common.properties=available\n" + "\n"))

    System.setProperties(system)
  }

}