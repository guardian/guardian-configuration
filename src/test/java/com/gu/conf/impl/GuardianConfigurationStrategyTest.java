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

package com.gu.conf.impl;

import com.gu.conf.Configuration;
import com.gu.conf.PropertyNotSetException;
import com.gu.conf.fixtures.PropertiesBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuardianConfigurationStrategyTest {

   private static String SYSTEM_OVERRIDE_PROPERTIES = "System";

   private static String DEV_OVERRIDE_SYS_PROPERTIES = String.format("file://%s/.gu/webapp.properties", System.getProperty("user.home"));
   private static String SYS_PROPERTIES = "file:///etc/gu/webapp.properties";
   private static String ENVIRONMENTAL_APPLICATION_PROPERTIES = "classpath:/conf/gudev.gnl.webapp.properties";
   private static String STAGE_PROPERTIES = "classpath:/conf/DEV.properties";
   private static String ENVIRONMENTAL_PROPERTIES = "classpath:/conf/gudev.gnl.properties";
   private static String GLOBAL_PROPERTIES = "classpath:/conf/global.properties";

   private static String INSTALLATION_PROPERTIES = "file:/etc/gu/installation.properties";

   @Mock
   FileAndResourceLoader fileLoader;
   @Mock
   InstallationConfiguration installation;
   GuardianConfigurationStrategy strategy;

   @Before
   public void setUp() throws IOException {
      when(installation.getServiceDomain()).thenReturn("gudev.gnl");
      when(installation.getStage()).thenReturn("DEV");

      // DEV_OVERRIDE_SYS_PROPERTIES
      AbstractConfiguration configuration = new ConfigurationBuilder()
         .identifier(DEV_OVERRIDE_SYS_PROPERTIES)
         .property("dev.override.sys.properties", "available")
         .property("source", "dev.override.sys.properties")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(DEV_OVERRIDE_SYS_PROPERTIES)).thenReturn(configuration);

      // SYS_PROPERTIES
      configuration = new ConfigurationBuilder()
         .identifier(SYS_PROPERTIES)
         .property("sys.properties", "available")
         .property("source", "sys.properties")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(SYS_PROPERTIES)).thenReturn(configuration);

      // ENVIRONMENTAL_APPLICATION_PROPERTIES
      configuration = new ConfigurationBuilder()
         .identifier(ENVIRONMENTAL_APPLICATION_PROPERTIES)
         .property("env.application.properties", "available")
         .property("source", "env.application.properties")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(ENVIRONMENTAL_APPLICATION_PROPERTIES)).thenReturn(configuration);

      // STAGE_PROPERTIES
      configuration = new ConfigurationBuilder()
         .identifier(STAGE_PROPERTIES)
         .property("stage.properties", "available")
         .property("source", "stage.properties")
         .property("webapp.stage.properties.precendence", "stage.properties")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(STAGE_PROPERTIES)).thenReturn(configuration);

      // ENVIRONMENTAL_PROPERTIES
      configuration = new ConfigurationBuilder()
         .identifier(ENVIRONMENTAL_PROPERTIES)
         .property("env.dev.properties", "available")
         .property("source", "env.dev.properties")
         .property("webapp.stage.properties.precendence", "env.dev.properties")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(ENVIRONMENTAL_PROPERTIES)).thenReturn(configuration);

      // GLOBAL_DEV_PROPERTIES
      configuration = new ConfigurationBuilder()
         .identifier(GLOBAL_PROPERTIES)
         .property("global.dev.properties", "available")
         .property("source", "global.dev.properties")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(GLOBAL_PROPERTIES)).thenReturn(configuration);

      // INSTALLATION_PROPERTIES
      configuration = new ConfigurationBuilder()
         .identifier(INSTALLATION_PROPERTIES)
         .property("installation.properties", "available")
         .property("source", "installation.properties")
         .property("int.service.domain", "gudev.gnl")
         .property("stage", "DEV")
         .toConfiguration();
      when(fileLoader.getConfigurationFrom(INSTALLATION_PROPERTIES)).thenReturn(configuration);


      strategy = new GuardianConfigurationStrategy(fileLoader, installation);
   }

   @Test
   public void shouldCheckInstallationPropertiesAreNotAvailableToApplications() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.hasProperty("installation.properties"), is(false));
      assertThat(configuration.getStringProperty("source"), not("installation.properties"));
      assertThat(configuration.hasProperty("int.service.domain"), is(false));
      assertThat(configuration.hasProperty("stage"), is(false));
   }

   @Test
   public void shouldOverridePropertiesWithSystemPropertiesIfDefined() throws IOException, PropertyNotSetException {
      try {
         System.setProperty("source", "system.override.property");
         System.setProperty("random.system.property", "meh");
         Configuration configuration = strategy.getConfiguration("webapp", "/conf");

         assertThat(configuration.getStringProperty("source"), is("system.override.property"));
         assertThat(configuration.hasProperty("random.system.property"), is(false));
      } finally {
         System.clearProperty("source");
         System.clearProperty("random.system.property");
      }
   }

   @Test
   public void shouldLoadDevOverrideSysProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.getStringProperty("dev.override.sys.properties"), is("available"));
      assertThat(configuration.hasProperty("no-property"), is(false));
   }

   @Test
   public void shouldLoadSystemWebappProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.getStringProperty("sys.properties"), is("available"));
      assertThat(configuration.hasProperty("no-property"), is(false));
   }

   @Test
   public void shouldLoadWebappServiceDomainApplicationProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.getStringProperty("env.application.properties"), is("available"));
      assertThat(configuration.hasProperty("no-property"), is(false));
   }

   @Test
   public void shouldLoadWebappStageProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.getStringProperty("stage.properties"), is("available"));
      assertThat(configuration.hasProperty("no-property"), is(false));
   }

   @Test
   public void shouldLoadWebappServiceDomainProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.getStringProperty("env.dev.properties"), is("available"));
      assertThat(configuration.hasProperty("no-property"), is(false));
   }

   @Test
   public void shouldLoadWebappGlobalProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");

      assertThat(configuration, notNullValue());
      assertThat(configuration.getStringProperty("global.dev.properties"), is("available"));
      assertThat(configuration.hasProperty("no-property"), is(false));
   }

   @Test
   public void shouldProvideWebappStagePropertiesInPreferenceToServiceDomainProperties() throws IOException, PropertyNotSetException {
      Configuration configuration = strategy.getConfiguration("webapp", "/conf");
      assertThat(configuration.getStringProperty("webapp.stage.properties.precendence"), is("stage.properties"));
   }

   @Test
   public void shouldHavePrettyToString() throws IOException {
      Properties system = System.getProperties();
      System.setProperties(new PropertiesBuilder().
         property("user.home", system.getProperty("user.home")).
         toProperties());

      Configuration configuration = strategy.getConfiguration("webapp", "/conf");
      assertThat(configuration.toString(), is(
         "# Properties from System\n" +
         "\n" +
         "# Properties from file://" + System.getProperty("user.home") + "/.gu/webapp.properties\n" +
         "dev.override.sys.properties=available\n" +
         "source=dev.override.sys.properties\n" +
         "\n" +
         "# Properties from file:///etc/gu/webapp.properties\n" +
         "sys.properties=available\n" +
         "\n" +
         "# Properties from classpath:/conf/gudev.gnl.webapp.properties\n" +
         "env.application.properties=available\n" +
         "\n" +
         "# Properties from classpath:/conf/DEV.properties\n" +
         "stage.properties=available\n" +
         "webapp.stage.properties.precendence=stage.properties\n" +
         "\n" +
         "# Properties from classpath:/conf/gudev.gnl.properties\n" +
         "env.dev.properties=available\n" +
         "\n" +
         "# Properties from classpath:/conf/global.properties\n" +
         "global.dev.properties=available\n" +
         "\n"
      ));

      System.setProperties(system);
   }
}