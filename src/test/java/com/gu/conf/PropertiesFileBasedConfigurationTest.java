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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PropertiesFileBasedConfigurationTest extends ConfigurationAdaptorTestBase {

   @Before
   public void setUp() {
      PropertiesBuilder properties = new PropertiesBuilder();
      properties.property("precendence.test.property", "first");
      properties.property("double.property", "25.0");
      properties.property("integer.property", "23");
      properties.property("nonnumeric.property", "qwe");
      properties.property("list.property", "rimbaud,verlaine");

      configuration = new PropertiesFileBasedConfiguration(properties.toProperties(), "properties");
   }

   @Test
   public void shouldGetPropertySource() {
      String propertySource = configuration.getPropertySource("nonnumeric.property");
      assertThat(propertySource, is("properties"));
   }

   @Test
   public void shouldToStringInStandardFormat() {
      String expected =
         "# Properties from " + configuration.getIdentifier() + "\n" +
         "double.property=25.0\n" +
         "integer.property=23\n" +
         "list.property=rimbaud,verlaine\n" +
         "nonnumeric.property=qwe\n" +
         "precendence.test.property=first\n" +
         "\n";

      assertThat(configuration.toString(), is(expected));
   }
}