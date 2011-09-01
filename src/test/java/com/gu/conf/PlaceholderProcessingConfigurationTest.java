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
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaceholderProcessingConfigurationTest extends ConfigurationAdaptorTestBase {

   @Mock private PlaceholderResolver placeholderResolver;

   @Before
   public void setUp() throws PropertyNotSetException {
      MapBasedConfiguration mapBasedConfiguration = new MapBasedConfiguration("test");

      mapBasedConfiguration.add("double.property", "25.0");
      mapBasedConfiguration.add("precendence.test.property", "second");
      mapBasedConfiguration.add("integer.property", "23");
      mapBasedConfiguration.add("nonnumeric.property", "qwe");
      mapBasedConfiguration.add("list.property", "rimbaud,verlaine");
      mapBasedConfiguration.add("utility.property", "has a ${placeholder}");

      when(placeholderResolver.resolvePlaceholder("placeholder")).thenReturn("resolved placeholder");

      configuration = new PlaceholderProcessingConfiguration(
         mapBasedConfiguration, placeholderResolver);
   }

   @Test
   public void shouldReplacePlaceholderWithResolvedValue() throws PropertyNotSetException {
      assertThat(configuration.hasProperty("utility.property"), is(true));
      assertThat(configuration.getStringProperty("utility.property"), is("has a resolved placeholder"));
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
         "utility.property=has a resolved placeholder\n" +
         "\n";

      assertThat(configuration.toString(), Matchers.is(expected));
   }
}
