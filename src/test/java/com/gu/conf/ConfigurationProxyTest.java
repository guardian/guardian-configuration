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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConfigurationProxyTest extends ConfigurationAdaptorTestBase {

   private Configuration original;

   @Before
   public void setUp() {
      MapBasedConfiguration proxiedConfiguration = new MapBasedConfiguration("test");

      proxiedConfiguration.add("double.property", "25.0");
      proxiedConfiguration.add("precendence.test.property", "second");
      proxiedConfiguration.add("integer.property", "23");
      proxiedConfiguration.add("nonnumeric.property", "qwe");
      proxiedConfiguration.add("list.property", "rimbaud,verlaine");
      proxiedConfiguration.add("utility.property", "utility");


      configuration = new ConfigurationProxy(proxiedConfiguration);
      original = proxiedConfiguration;
   }


   @Test
   public void shouldGetPropertyNamesAsOriginal() {
      assertThat(configuration.getPropertyNames(), is(original.getPropertyNames()));
   }

   @Test
   public void shouldHaveSamePropertyValuesAsOriginal() throws PropertyNotSetException {
      for (String property : original.getPropertyNames()) {
         assertThat(configuration.hasProperty(property), is(true));
         assertThat(configuration.getStringProperty(property), is(original.getStringProperty(property)));
      }
   }

   @Test
   public void shouldGetPropertySourceAsOriginal() {
      for (String property : original.getPropertyNames()) {
         assertThat(configuration.getPropertySource(property), is(original.getPropertySource(property)));
      }
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