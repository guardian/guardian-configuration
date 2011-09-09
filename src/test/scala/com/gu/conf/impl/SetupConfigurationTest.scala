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
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is
import org.hamcrest.Matchers.nullValue
import org.mockito.Mockito.when

@RunWith(classOf[MockitoJUnitRunner])
class SetupConfigurationTest {

  @Mock
  var loader: FileAndResourceLoader = _

  private def installationConfiguration(): SetupConfiguration = {
    installationConfiguration(new ConfigurationBuilder().toConfiguration)
  }

  private def installationConfiguration(key: String, value: String): SetupConfiguration = {
    installationConfiguration(new ConfigurationBuilder().property(key, value).toConfiguration)
  }

  private def installationConfiguration(installationProperties: AbstractConfiguration): SetupConfiguration = {
    when(loader.getConfigurationFrom("file:///etc/gu/install_vars")).thenReturn(installationProperties)
    new SetupConfiguration(loader)
  }

  @Test
  def shouldNotReturnTheValueOfTheIntServiceDomainSystemPropertyIfSet() {
    try {
      System.setProperty("int.service.domain", "myintservicedomain")
      assertThat(installationConfiguration().getServiceDomain, is("default"))
    } finally {
      System.clearProperty("int.service.domain")
    }
  }

  @Test
  def shouldReadValueFromInstallationPropertiesFile() {
    assertThat(System.getProperty("int.service.domain"), nullValue())

    val configuration = installationConfiguration("INT_SERVICE_DOMAIN", "myservicedomain")
    assertThat(configuration.getServiceDomain, is("myservicedomain"))
  }

  @Test
  def shouldNotReturnTheStageFromSystemPropertyIfSet() {
    try {
      System.setProperty("stage", "SYSTEMPROPERTYSTAGE")
      assertThat(installationConfiguration().getStage, is("default"))
    } finally {
      System.clearProperty("stage")
    }
  }

  @Test
  def shouldReadStageFromInstallationPropertiesFileIfNoSystemPropertySet() {
    assertThat(System.getProperty("stage"), nullValue())
    val configuration = installationConfiguration("STAGE", "STAGEFROMPROPERTIES")
    assertThat(configuration.getStage, is("STAGEFROMPROPERTIES"))
  }

  @Test
  def shouldDefaultWhenInstallationPropertiesFileExistsButDoesNotContainValue() {
    assertThat(installationConfiguration().getServiceDomain, is("default"))
  }
}