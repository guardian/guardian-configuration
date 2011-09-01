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

import com.google.common.collect.ImmutableList;
import com.gu.conf.exceptions.PropertyNotSetException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class ListOfPropertiesWithSourceBasedConfigurationTest extends ConfigurationAdaptorTestBase {

   @Before
   public void setUp() {
      PropertiesBuilder sysProperties = new PropertiesBuilder();
      sysProperties.property("precendence.test.property", "first");
      sysProperties.property("double.property", "25.0");
      sysProperties.source("file:///sys.properties");

      PropertiesBuilder environmentalProperties = new PropertiesBuilder();
      environmentalProperties.property("precendence.test.property", "second");
      environmentalProperties.property("integer.property", "23");
      environmentalProperties.property("nonnumeric.property", "qwe");
      environmentalProperties.property("list.property", "rimbaud,verlaine");
      environmentalProperties.source("classpath:///env.dev.properties");

      ImmutableList<PropertiesWithSource> properties = ImmutableList.of(
         sysProperties.toPropertiesWithSource(),
         environmentalProperties.toPropertiesWithSource()
      );

      configuration = new ListOfPropertiesWithSourceBasedConfiguration(properties);
   }

   @Test
   public void shouldThrowForPropertyIfNotSet() {
      try {
         configuration.getStringProperty("nosuch.property");
         fail("exception expected");
      } catch (PropertyNotSetException ex) {
         assertThat(ex.getProperty(), is("nosuch.property"));
         assertThat(ex.getMessage(), is("Mandatory configuration property 'nosuch.property' was not found in any " +
            "of file:///sys.properties classpath:///env.dev.properties "));
      }
   }

   @Test
   public void shouldThrowForIntegerPropertyIfNotSet() {
      try {
         configuration.getIntegerProperty("nosuch.property");
         fail("exception expected");
      } catch (PropertyNotSetException ex) {
         assertThat(ex.getProperty(), is("nosuch.property"));
         assertThat(ex.getMessage(), is("Mandatory configuration property 'nosuch.property' was not found in any " +
            "of file:///sys.properties classpath:///env.dev.properties "));
      }
   }

   @Test
   public void shouldGetPropertySource() {
      String propertySource = configuration.getPropertySource("nonnumeric.property");
      assertThat(propertySource, is("classpath:///env.dev.properties"));
   }

   @Test
   public void shouldRespectFirstDeclarationPrecedenceInGetPropertySource() throws IOException {
      String propertySource = configuration.getPropertySource("precendence.test.property");
      assertThat(propertySource, is("file:///sys.properties"));
   }

   @Test
   public void shouldRespectFirstDeclarationPrecedenceInGetProperty() throws Exception {
      String property = configuration.getStringProperty("precendence.test.property");
      assertThat(property, is("first"));
   }

}