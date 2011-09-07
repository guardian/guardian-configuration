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

import com.gu.conf.PropertyNotSetException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaceholderResolverTest {
   private PlaceholderResolver placeholderResolver;

   @Mock private SystemEnvironmentConfiguration environment;
   @Mock private SystemPropertiesConfiguration system;

   @Before
   public void setUp() throws PropertyNotSetException {
      placeholderResolver = new PlaceholderResolver(environment, system);
      when(environment.getStringProperty("propname")).thenReturn("environment variable");
      when(system.getStringProperty("propname")).thenReturn("system property");
   }

   @Test
   public void shouldGetSystemProperty() throws PropertyNotSetException {
      assertThat(placeholderResolver.substitutePlaceholders("${propname}"), is("system property"));
   }

   @Test
   public void shouldGetEnvironmentVariable() throws PropertyNotSetException {
      assertThat(placeholderResolver.substitutePlaceholders("${env.propname}"), is("environment variable"));
   }
}
