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

import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io._
import java.net.URL
import java.util.Properties
import java.util.regex.Pattern

private[conf] class FileAndResourceLoader {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[FileAndResourceLoader])
  private val protocolMatcher: Pattern = Pattern.compile("(?:file|classpath):(?://)?(.*)")

  private def stripProtocol(location: String): String = {
    val `match` = protocolMatcher.matcher(location)
    if (`match`.matches) {
      return `match`.group(1)
    }

    location
  }

  private def getFile(resource: String): InputStream = {
    val file: File = new File(stripProtocol(resource))
    if (!file.canRead) {
      LOG.info("Ignoring missing configuration file " + resource)
      return null
    }

    new BufferedInputStream(new FileInputStream(file))
  }

  private def getResource(resource: String): InputStream = {
    val classloader = classOf[FileAndResourceLoader].getClassLoader
    var url: URL = classloader.getResource(stripProtocol(resource))
    if (url == null) {
      LOG.info("Ignoring missing configuration file " + resource)
      return null
    }
    var inputStream: InputStream = null
    try {
      inputStream = url.openStream
    } catch {
      case ioe: IOException => {
        LOG.warn("Cannot open resource trying to load properties from " + resource, ioe)
        return null
      }
    }

    new BufferedInputStream(inputStream)
  }

  def getConfigurationFrom(descriptor: String): AbstractConfiguration = {
    val properties = new Properties
    var inputStream: InputStream = null
    try {
      if (descriptor.startsWith("file:")) {
        inputStream = getFile(descriptor)
      } else if (descriptor.startsWith("classpath:")) {
        inputStream = getResource(descriptor)
      } else {
        throw new RuntimeException("Unknown protocol trying to load properties from " + descriptor)
      }
      if (inputStream != null) {
        properties.load(inputStream)
      }
    } catch {
      case ioe: IOException => {
        LOG.warn("unexpected error reading from file " + descriptor, ioe)
      }
    } finally {
      IOUtils.closeQuietly(inputStream)
    }

    new PropertiesFileBasedConfiguration(descriptor, properties)
  }
}