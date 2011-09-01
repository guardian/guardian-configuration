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

public class CompositeConfigurationTest extends ConfigurationAdaptorTestBase {

   @Before
   public void setUp() {
      MapBasedConfiguration conf1 = new MapBasedConfiguration("primary");
      conf1.add("precendence.test.property", "first");
      conf1.add("double.property", "25.0");


      MapBasedConfiguration conf2 = new MapBasedConfiguration("secondary");
      conf2.add("precendence.test.property", "second");
      conf2.add("integer.property", "23");
      conf2.add("nonnumeric.property", "qwe");
      conf2.add("list.property", "rimbaud,verlaine");

      configuration = new CompositeConfiguration(conf1, conf2);
   }

   @Test
   public void shouldGetPropertySource() {
      String propertySource = configuration.getPropertySource("nonnumeric.property");
      assertThat(propertySource, is("secondary"));
   }

   @Test
   public void shouldGivePrecedenceToFirstConfiguration() throws Exception {
      String property = configuration.getStringProperty("precendence.test.property");
      assertThat(property, is("first"));
   }

   @Test
   public void shouldToStringInStandardFormat() {
      String expected =
         "# Properties from primary\n" +
         "double.property=25.0\n" +
         "precendence.test.property=first\n" +
         "\n" +
         "# Properties from secondary\n" +
         "integer.property=23\n" +
         "list.property=rimbaud,verlaine\n" +
         "nonnumeric.property=qwe\n" +
         "\n";

      assertThat(configuration.toString(), is(expected));
   }
}