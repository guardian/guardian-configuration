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

import com.gu.conf.exceptions.PropertyNotSetException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProjectedConfigurationTest extends ConfigurationAdaptorTestBase {

   @Before
   public void setUp() {
      MapBasedConfiguration mapBasedConfiguration = new MapBasedConfiguration("test");

      mapBasedConfiguration.add("double.property", "25.0");
      mapBasedConfiguration.add("precendence.test.property", "second");
      mapBasedConfiguration.add("integer.property", "23");
      mapBasedConfiguration.add("nonnumeric.property", "qwe");
      mapBasedConfiguration.add("list.property", "rimbaud,verlaine");

      mapBasedConfiguration.add("projected.out", "lost");

      Set<String> projectionNames = new HashSet<String>(asList(
         "precendence.test.property",
         "double.property",
         "integer.property",
         "nonnumeric.property",
         "list.property")
      );

      configuration = new ProjectedConfiguration(mapBasedConfiguration, projectionNames);
   }

   @Test
   public void shouldContainedProjectedProperties() {
      assertThat(configuration.size(), is(5));
      assertThat(configuration.hasProperty("precendence.test.property"), is(true));
      assertThat(configuration.hasProperty("double.property"), is(true));
      assertThat(configuration.hasProperty("integer.property"), is(true));
      assertThat(configuration.hasProperty("nonnumeric.property"), is(true));
      assertThat(configuration.hasProperty("list.property"), is(true));
   }

   @Test
   public void shouldNotContainedProjectedOutProperties() {
      assertThat(configuration.hasProperty("projected.out"), is(false));
   }

   @Test
   public void shouldHaveSamePropertyValuesAsOriginal() throws PropertyNotSetException {
      assertThat(configuration.getStringProperty("precendence.test.property"), is("second"));
      assertThat(configuration.getStringProperty("double.property"), is("25.0"));
      assertThat(configuration.getStringProperty("integer.property"), is("23"));
      assertThat(configuration.getStringProperty("nonnumeric.property"), is("qwe"));
      assertThat(configuration.getStringProperty("list.property"), is("rimbaud,verlaine"));
   }

   @Test
   public void shouldGetPropertySource() {
      String propertySource = configuration.getPropertySource("nonnumeric.property");
      assertThat(propertySource, is("test"));
   }

   @Test
   public void shouldToStringInStandardFormat() {
      String expected =
         "# Properties from " + configuration.getIdentifier() + "\n" +
         "double.property=25.0\n" +
         "integer.property=23\n" +
         "list.property=rimbaud,verlaine\n" +
         "nonnumeric.property=qwe\n" +
         "precendence.test.property=second\n" +
         "\n";

      assertThat(configuration.toString(), is(expected));
   }
}