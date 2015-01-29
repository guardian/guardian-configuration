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
import java.util.Properties
import org.mockito.Mockito.when
import org.scalatest.{ FunSuite, BeforeAndAfter }
import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar

class SetupConfigurationTest extends FunSuite with Matchers with MockitoSugar with BeforeAndAfter {

  var loader = mock[PropertiesLoader]

  private def installationConfiguration(): SetupConfiguration = {
    installationConfiguration(new PropertiesBuilder().toProperties)
  }

  private def installationConfiguration(key: String, value: String): SetupConfiguration = {
    installationConfiguration(new PropertiesBuilder().property(key, value).toProperties)
  }

  private def installationConfiguration(installationProperties: Properties): SetupConfiguration = {
    when(loader.getPropertiesFrom("file:///etc/gu/install_vars")).thenReturn(installationProperties)
    new SetupConfiguration(loader)
  }

  test("should not return the value of the int service domain system property if set") {
    try {
      System.setProperty("int.service.domain", "myintservicedomain")
      installationConfiguration().getServiceDomain should be("default")
    } finally {
      System.clearProperty("int.service.domain")
    }
  }

  test("should read value from installation properties file") {
    System.getProperty("int.service.domain") should be(null)

    val configuration = installationConfiguration("INT_SERVICE_DOMAIN", "myservicedomain")
    configuration.getServiceDomain should be("myservicedomain")
  }

  test("should not return the stage from system property if set") {
    try {
      System.setProperty("stage", "SYSTEMPROPERTYSTAGE")
      installationConfiguration().getStage should be("default")
    } finally {
      System.clearProperty("stage")
    }
  }

  test("should read stage from installation properties file if no system property set") {
    System.getProperty("stage") should be(null)
    val configuration = installationConfiguration("STAGE", "STAGEFROMPROPERTIES")
    configuration.getStage should be("STAGEFROMPROPERTIES")
  }

  test("should read stage from installation properties file and translate to appropriate stage if stage delimiter used ('___') in stage name") {
    System.getProperty("stage") should be(null)
    val configuration = installationConfiguration("STAGE", "CODE___TEST-STAGE-NAME")
    println(configuration.getStage)
    configuration.getStage should be("CODE")
  }

  test("should default when installation properties file exists but does not contain value") {
    installationConfiguration().getServiceDomain should be("default")
  }
}