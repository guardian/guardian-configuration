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
import org.mockito.invocation.InvocationOnMock
import org.mockito.runners.MockitoJUnitRunner
import org.mockito.stubbing.Answer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.is
import org.mockito.Matchers.anyString
import org.mockito.Mockito.when

@RunWith(classOf[MockitoJUnitRunner])
class PlaceholderProcessingConfigurationTest extends AbstractConfigurationTestBase {

  @Mock
  var placeholderResolver: PlaceholderResolver = _

  @Before
  def setUp() {
    val mapBasedConfiguration = new MapBasedConfiguration("test")
    mapBasedConfiguration.add("double.property", "25.0")
    mapBasedConfiguration.add("precendence.test.property", "second")
    mapBasedConfiguration.add("integer.property", "23")
    mapBasedConfiguration.add("nonnumeric.property", "qwe")
    mapBasedConfiguration.add("list.property", "rimbaud,verlaine")
    mapBasedConfiguration.add("utility.property", "has a ${placeholder}")

    when(placeholderResolver.substitutePlaceholders(anyString())).thenAnswer(new Answer[String] {
      def answer(invocation: InvocationOnMock): String = {
        val arg = invocation.getArguments()(0).toString
        arg.replaceAll("\\$\\{placeholder\\}", "resolved placeholder")
      }
    })

    configuration = new PlaceholderProcessingConfiguration(mapBasedConfiguration, placeholderResolver)
  }

  @Test
  def shouldReplacePlaceholderWithResolvedValue() {
    assertThat(configuration.hasProperty("utility.property"), is(true))
    assertThat(configuration.getStringProperty("utility.property").get, is("has a resolved placeholder"))
  }

  @Test
  def shouldToStringInStandardFormat() {
    val expected =
      "# Properties from " + configuration.getIdentifier + "\n" +
        "double.property=25.0\n" +
        "integer.property=23\n" +
        "list.property=rimbaud,verlaine\n" +
        "nonnumeric.property=qwe\n" +
        "precendence.test.property=second\n" +
        "utility.property=has a resolved placeholder\n" +
        "\n"

    assertThat(configuration.toString(), is(expected))
  }
}