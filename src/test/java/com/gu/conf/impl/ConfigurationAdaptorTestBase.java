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
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

public abstract class ConfigurationAdaptorTestBase {

   protected AbstractConfiguration configuration;

   @Test
   public void shouldHaveCorrectSize() {
      assertThat(configuration.size(), is(6));
   }

   @Test
   public void shouldHaveCorrectTestData() throws PropertyNotSetException {
      assertThat(configuration.hasProperty("precendence.test.property"), is(true));
      assertThat(configuration.hasProperty("utility.property"), is(true));

      assertThat(configuration.getStringProperty("double.property"), is("25.0"));
      assertThat(configuration.getStringProperty("integer.property"), is("23"));
      assertThat(configuration.getStringProperty("nonnumeric.property"), is("qwe"));
      assertThat(configuration.getStringProperty("list.property"), is("rimbaud,verlaine"));
   }

   @Test
   public void shouldGetNullForPropertySourceIfNotSet() {
      Configuration propertySource = configuration.getPropertySource("nosuch.property");
      assertThat(propertySource, nullValue());
   }

   @Test
   public void shouldGetFalseForHasPropertyIfNotSet() {
      assertThat(configuration.hasProperty("nosuch.property"), is(false));
   }

   @Test
   public void shouldGetTrueForHasPropertyIfSet() {
      assertThat(configuration.hasProperty("nonnumeric.property"), is(true));
   }

   @Test
   public void shouldGetProperty() throws Exception {
      String property = configuration.getStringProperty("nonnumeric.property");
      assertThat(property, is("qwe"));
   }

   @Test
   public void shouldThrowForPropertyIfNotSet() {
      try {
         configuration.getStringProperty("nosuch.property");
         fail("exception expected");
      } catch (PropertyNotSetException ex) {
         assertThat(ex.getProperty(), is("nosuch.property"));
         assertThat(ex.getMessage(), is("Mandatory configuration property 'nosuch.property' was not found."));
      }
   }

   @Test
   public void shouldGetDefaultForPropertyIfNotSet() {
      String property = configuration.getStringProperty("nosuch.property", "default");
      assertThat(property, is("default"));
   }

   @Test
   public void shouldGetIntegerProperty() throws Exception {
      Integer property = configuration.getIntegerProperty("integer.property");
      assertThat(property, is(23));
   }

   @Test
   public void shouldThrowForIntegerPropertyIfNotSet() {
      try {
         configuration.getIntegerProperty("nosuch.property");
         fail("exception expected");
      } catch (PropertyNotSetException ex) {
         assertThat(ex.getProperty(), is("nosuch.property"));
         assertThat(ex.getMessage(), is("Mandatory configuration property 'nosuch.property' was not found."));
      }
   }

   @Test
   public void shouldThrowForIntegerPropertyIfNotInteger() throws PropertyNotSetException {
      try {
         configuration.getIntegerProperty("double.property");
         fail("exception expected");
      } catch (NumberFormatException ex) {
         //expected
      }

      try {
         configuration.getIntegerProperty("nonnumeric.property");
         fail("exception expected");
      } catch (NumberFormatException ex) {
         //expected
      }
   }

   @Test
   public void shouldGetDefaultForIntegerPropertyIfNotSet() throws IOException {
      Integer property = configuration.getIntegerProperty("nosuch.property", 34);
      assertThat(property, is(34));
   }

   @Test
   public void shouldGetDefaultForIntegerPropertyIfNotInteger() throws IOException {
      Integer property = configuration.getIntegerProperty("double.property", 45);
      assertThat(property, is(45));

      property = configuration.getIntegerProperty("nonnumeric.property", 65);
      assertThat(property, is(65));
   }

   @Test
   public void shouldGetPropertyList() throws Exception {
      List<String> properties = configuration.getStringPropertiesSplitByComma("list.property");
      assertThat(properties.size(), is(2));
      assertThat(properties.get(0), is("rimbaud"));
      assertThat(properties.get(1), is("verlaine"));
   }

   @Test
   public void shouldGetPropertyNames() throws Exception {
      Set<String> names = configuration.getPropertyNames();
      assertThat(names.size(), is(6));
      assertThat(names, hasItem("precendence.test.property"));
      assertThat(names, hasItem("double.property"));
      assertThat(names, hasItem("integer.property"));
      assertThat(names, hasItem("nonnumeric.property"));
      assertThat(names, hasItem("list.property"));
      assertThat(names, hasItem("utility.property"));
   }

   @Test
   public void shouldToPropertiesWithSameProperties() throws PropertyNotSetException {
      Properties properties = configuration.toProperties();
      for (String property : configuration.getPropertyNames()) {
         assertThat(properties.getProperty(property), is(configuration.getStringProperty(property)));
      }

      assertThat(properties.size(), is(configuration.size()));
   }

   @Test
   public void shouldProjectCorrectly() throws PropertyNotSetException {
      Set<String> projectionNames = new HashSet<String>(asList(
         "precendence.test.property",
         "double.property",
         "integer.property",
         "nonnumeric.property",
         "utility.property")
      );
      AbstractConfiguration projection = configuration.project(projectionNames);

      assertThat(projection.size(), is(5));
      assertThat(projection.hasProperty("precendence.test.property"), is(true));
      assertThat(projection.hasProperty("double.property"), is(true));
      assertThat(projection.hasProperty("integer.property"), is(true));
      assertThat(projection.hasProperty("nonnumeric.property"), is(true));
      assertThat(projection.hasProperty("utility.property"), is(true));

      for (String property : projection.getPropertyNames()) {
         assertThat(projection.getStringProperty(property), is(configuration.getStringProperty(property)));
      }
   }

   @Test
   public void shouldMinusCorrectly() throws PropertyNotSetException {
      Set<String> minusNames = new HashSet<String>(asList(
         "precendence.test.property")
      );
      AbstractConfiguration projection = configuration.minus(minusNames);

      assertThat(projection.size(), is(5));
      assertThat(projection.hasProperty("double.property"), is(true));
      assertThat(projection.hasProperty("integer.property"), is(true));
      assertThat(projection.hasProperty("nonnumeric.property"), is(true));
      assertThat(projection.hasProperty("list.property"), is(true));
      assertThat(projection.hasProperty("utility.property"), is(true));

      for (String property : projection.getPropertyNames()) {
         assertThat(projection.getStringProperty(property), is(configuration.getStringProperty(property)));
      }
   }

   @Test
   public void shouldOverride() throws PropertyNotSetException {
      MapBasedConfiguration overrides = new MapBasedConfiguration("overrides");
      overrides.add("utility.property", "overriden");

      AbstractConfiguration withOverrides = configuration.overrideWith(overrides);

      assertThat(withOverrides.size(), is(6));
      assertThat(withOverrides.hasProperty("double.property"), is(true));
      assertThat(withOverrides.hasProperty("precendence.test.property"), is(true));
      assertThat(withOverrides.hasProperty("integer.property"), is(true));
      assertThat(withOverrides.hasProperty("nonnumeric.property"), is(true));
      assertThat(withOverrides.hasProperty("list.property"), is(true));
      assertThat(withOverrides.hasProperty("utility.property"), is(true));

      assertThat(withOverrides.getStringProperty("utility.property"), is("overriden"));

      Set<String> unchanged = new HashSet<String>();
      unchanged.addAll(withOverrides.getPropertyNames());
      unchanged.remove("utility.property");
      for (String property : unchanged) {
         assertThat(withOverrides.getStringProperty(property), is(configuration.getStringProperty(property)));
      }
   }

}