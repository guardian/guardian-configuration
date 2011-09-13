package com.gu.conf.impl

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class PropertiesLoaderTest extends FunSuite with ShouldMatchers {
  val loader = new PropertiesLoader

  test("should load classpath based properties") {
    val properties = loader getPropertiesFrom "classpath:conf/test.properties"

    properties.getProperty("property") should be("theft")
  }

  test("should load file based properties") {
    val properties = loader getPropertiesFrom "file:./src/test/resources/conf/test.properties"

    properties.getProperty("property") should be("theft")
  }

  test("should ignore missing classpath based properties when not available") {
    val properties = loader getPropertiesFrom "classpath:conf/does-not-exist.properties"

    properties.size() should be(0)
  }

  test("should ignore missing file based properties when not available") {
    val properties = loader getPropertiesFrom "file:./src/test/resources/conf/does-not-exist.properties"

    properties.size should be(0)
  }

  test("should ignore unknown protocols") {
    val properties = loader getPropertiesFrom "unknown:protocol"

    properties.size should be(0)
  }
}