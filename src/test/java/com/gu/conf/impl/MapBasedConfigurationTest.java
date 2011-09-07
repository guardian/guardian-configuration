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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MapBasedConfigurationTest extends AbstractConfigurationTestBase {

   @Before
   public void setUp() {
      MapBasedConfiguration mapBasedConfiguration = new MapBasedConfiguration("test");

      mapBasedConfiguration.add("precendence.test.property", "first");
      mapBasedConfiguration.add("double.property", "25.0");

      mapBasedConfiguration.add("precendence.test.property", "second");
      mapBasedConfiguration.add("integer.property", "23");
      mapBasedConfiguration.add("nonnumeric.property", "qwe");
      mapBasedConfiguration.add("list.property", "rimbaud,verlaine");
      mapBasedConfiguration.add("utility.property", "utility");

      configuration = mapBasedConfiguration;
   }

   @Test
   public void shouldGetPropertySource() {
      AbstractConfiguration propertySource = configuration.getPropertySource("nonnumeric.property");
      assertThat(propertySource, is(configuration));
   }

   @Test
   public void shouldNotRespectFirstDeclarationPrecedenceInGetProperty() throws Exception {
      String property = configuration.getStringProperty("precendence.test.property");
      assertThat(property, is("second"));
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
         "utility.property=utility\n" +
         "\n";

      assertThat(configuration.toString(), is(expected));
   }
}