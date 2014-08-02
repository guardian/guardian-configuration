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
import java.io.File

class PropertiesLoaderTest extends FunSuite with Matchers {
  val loader = new PropertiesLoader
  val basedir = "file:" + new File(".").getAbsolutePath

  test("should load classpath based properties") {
    val properties = loader getPropertiesFrom "classpath:conf/test.properties"

    properties.getProperty("property") should be("theft")
  }

  test("should load file based properties") {
    val properties = loader getPropertiesFrom (basedir + "/src/test/resources/conf/test.properties")

    properties.getProperty("property") should be("theft")
  }

  test("should ignore missing classpath based properties when not available") {
    val properties = loader getPropertiesFrom "classpath:conf/does-not-exist.properties"

    properties.size() should be(0)
  }

  test("should ignore missing file based properties when not available") {
    val properties = loader getPropertiesFrom (basedir + "/src/test/resources/conf/does-not-exist.properties")

    properties.size should be(0)
  }

  test("should ignore unknown protocols") {
    val properties = loader getPropertiesFrom "unknown:protocol"

    properties.size should be(0)
  }
}