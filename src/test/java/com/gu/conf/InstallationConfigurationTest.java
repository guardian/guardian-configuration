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

package com.gu.conf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.gu.conf.InstallationConfiguration.INSTALLATION_PROPERTIES_LOCATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InstallationConfigurationTest {

   @Mock private FileAndResourceLoader loader;

   private InstallationConfiguration installationConfiguration() {
      return installationConfiguration(new ConfigurationBuilder().toConfiguration());
   }

   private InstallationConfiguration installationConfiguration(String key, String value) {
      return installationConfiguration(new ConfigurationBuilder().
         property(key, value).
         toConfiguration());
   }

   private InstallationConfiguration installationConfiguration(
      Configuration installationProperties) {
      when(loader.getConfigurationFrom(INSTALLATION_PROPERTIES_LOCATION)).
         thenReturn(installationProperties);

      return new InstallationConfiguration(loader);
   }

   @Test
   public void shouldReturnTheValueOfTheIntServiceDomainSystemPropertyIfSet() throws Exception {
      try {
         System.setProperty("int.service.domain", "myintservicedomain");
         InstallationConfiguration configuration = installationConfiguration();

         assertThat(configuration.getServiceDomain(), is("myintservicedomain"));
      } finally {
         System.clearProperty("int.service.domain");
      }
   }

   @Test
   public void shouldReadValueFromInstallationPropertiesFile() throws Exception {
      assertThat(System.getProperty("int.service.domain"), is(nullValue()));
      InstallationConfiguration configuration = installationConfiguration("INT_SERVICE_DOMAIN", "myservicedomain");

      assertThat(configuration.getServiceDomain(), is("myservicedomain"));
   }


   @Test
   public void shouldReturnTheStageFromSystemPropertyIfSet() throws Exception {
      try {
         System.setProperty("stage", "SYSTEMPROPERTYSTAGE");
         InstallationConfiguration configuration = installationConfiguration();

         assertThat(configuration.getStage(), is("SYSTEMPROPERTYSTAGE"));
      } finally {
         System.clearProperty("stage");
      }
   }

   @Test
   public void shouldReadStageFromInstallationPropertiesFileIfNoSystemPropertySet() throws Exception {
      assertThat(System.getProperty("stage"), is(nullValue()));

      InstallationConfiguration configuration = installationConfiguration("STAGE", "STAGEFROMPROPERTIES");

      assertThat(configuration.getStage(), is("STAGEFROMPROPERTIES"));
   }

   @Test
   public void shouldDefaultWhenInstallationPropertiesFileExistsButDoesNotContainValue() throws Exception {
      InstallationConfiguration configuration = installationConfiguration();
      assertThat(configuration.getServiceDomain(), is("default"));
   }

}
