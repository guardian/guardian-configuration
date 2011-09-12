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
import org.scalatest.matchers.ShouldMatchers
import com.gu.conf.impl.PrinterUtil.propertyString

class PrinterUtilTest extends FunSuite with ShouldMatchers {
  test("should replace password with placeholder") {
    propertyString("password", "abc123") should endWith("*** PASSWORD ****\n")
    propertyString("foo.password.blah", "abc123") should endWith("*** PASSWORD ****\n")
    propertyString("blah.pass.foo", "abc123") should endWith("*** PASSWORD ****\n")
  }

  test("should replace key with placeholder") {
    propertyString("key", "abc123") should endWith("*** KEY ****\n")
    propertyString("foo.key.blah", "abc123") should endWith("*** KEY ****\n")
  }

  test("should format key value") {
    propertyString("akey", "value") should be("akey=value\n")
  }
}