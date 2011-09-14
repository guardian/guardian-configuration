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

import com.gu.conf.vfs.ClasspathFileProvider
import java.io._
import java.util.Properties
import org.apache.commons.io.IOUtils
import org.apache.commons.vfs2.impl.DefaultFileSystemManager
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider
import org.apache.commons.vfs2.provider.jar.JarFileProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private[conf] class PropertiesLoader {
  private val LOG: Logger = LoggerFactory.getLogger(classOf[PropertiesLoader])

  private val vfs = {
    val manager = new DefaultFileSystemManager

    manager.addProvider("classpath", new ClasspathFileProvider)
    manager.addProvider("file", new DefaultLocalFileProvider)

    val jarProvider = new JarFileProvider
    manager.addProvider("jar", jarProvider)
    manager.addProvider("war", jarProvider)

    manager.init()

    manager
  }

  def getPropertiesFrom(descriptor: String): Properties = {
    val properties = new Properties
    var inputStream: Option[InputStream] = None

    try {
      val file = Option(vfs resolveFile descriptor) filter { _.exists }
      if (!file.isDefined) {
        LOG.info("Ignoring missing configuration " + descriptor)
      }

      inputStream = file map { _.getContent.getInputStream }

      inputStream foreach { properties.load }

    } catch {
      case ioe: IOException =>
        LOG.warn("Unexpected error reading from " + descriptor, ioe)
    } finally {
      inputStream foreach { IOUtils.closeQuietly }
    }

    properties
  }
}