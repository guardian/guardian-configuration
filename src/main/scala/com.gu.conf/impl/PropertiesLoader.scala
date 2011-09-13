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
import java.util.Properties

private[conf] class PropertiesLoader {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[PropertiesLoader])
  private val Descriptor = "(file|classpath):(.*)".r

  private def getFile(location: String): Option[InputStream] = {
    val file = new File(location)

    file.canRead match {
      case true =>
        Some(new BufferedInputStream(new FileInputStream(file)))

      case false =>
        LOG.info("Ignoring missing configuration file file:" + location)
        None
    }
  }

  private def getResource(location: String): Option[InputStream] = {
    val url = Option(classOf[PropertiesLoader].getClassLoader getResource location)

    url match {
      case Some(existingUrl) => try {
        Some(new BufferedInputStream(existingUrl.openStream))
      } catch {
        case ioe: IOException => {
          LOG.warn("Cannot open resource trying to load properties from classpath:" + location, ioe)
          None
        }
      }

      case None =>
        LOG.info("Ignoring missing configuration file classpath:" + location)
        None
    }
  }

  def getPropertiesFrom(descriptor: String): Properties = {
    val properties = new Properties
    var inputStream: Option[InputStream] = None

    try {
      inputStream = descriptor match {
        case Descriptor("file", location) => getFile(location)
        case Descriptor("classpath", location) => getResource(location)
        case _ =>
          LOG.warn("Unknown protocol trying to load properties from " + descriptor)
          None
      }

      inputStream foreach { properties.load }

    } catch {
      case ioe: IOException =>
        LOG.warn("unexpected error reading from file " + descriptor, ioe)

    } finally {
      inputStream foreach { IOUtils.closeQuietly }
    }

    properties
  }
}