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
package com.gu.conf.impl

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.hamcrest.core.Is.is
import org.junit.Assert.assertThat
import org.mockito.Mockito.when

@RunWith(classOf[MockitoJUnitRunner])
class PlaceholderResolverTest {

  @Mock
  var environment: SystemEnvironmentConfiguration = _
  @Mock
  var system: SystemPropertiesConfiguration = _

  var placeholderResolver: PlaceholderResolver = _

  @Before
  def setUp() {
    placeholderResolver = new PlaceholderResolver(environment, system)

    when(environment.getStringProperty("propname")).thenReturn(Some("environment variable"))
    when(system.getStringProperty("propname")).thenReturn(Some("system property"))
  }

  @Test
  def shouldGetSystemProperty() {
    assertThat(placeholderResolver.substitutePlaceholders("${propname}"), is("system property"))
  }

  @Test
  def shouldGetEnvironmentVariable() {
    assertThat(placeholderResolver.substitutePlaceholders("${env.propname}"), is("environment variable"))
  }

}