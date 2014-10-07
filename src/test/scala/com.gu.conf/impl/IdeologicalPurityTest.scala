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

import org.scalatest.FunSuite
import org.scalatest.Matchers

class IdeologicalPurityTest extends FunSuite with Matchers {

  implicit def shouldBe2Is(actual: String) = new {
    def is(expected: String) = new StringShouldWrapper(actual) should be(expected)
  }

  test("should be ideologically pure") {
    val configuration = new ConfigurationBuilder().
      property("property", "theft").
      toConfiguration

    configuration("property") is ("theft")
  }
}
