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

import org.mockito.Mockito.when
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.mock.MockitoSugar

class PlaceholderResolverTest extends FunSuite with ShouldMatchers with MockitoSugar with BeforeAndAfter {

  var environment = mock[SystemEnvironmentConfiguration]
  var system = mock[SystemPropertiesConfiguration]

  var placeholderResolver: PlaceholderResolver = _

  before {
    placeholderResolver = new PlaceholderResolver(environment, system)

    when(environment.getStringProperty("propname")).thenReturn(Some("environment variable"))
    when(system.getStringProperty("propname")).thenReturn(Some("system property"))
  }

  test("should get system property") {
    placeholderResolver.substitutePlaceholders("${propname}") should be("system property")
  }

  test("should get environment variable") {
    placeholderResolver.substitutePlaceholders("${env.propname}") should be("environment variable")
  }

}