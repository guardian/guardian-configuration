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
package com.gu.conf.vfs

import org.apache.commons.vfs2.FileSystem
import org.apache.commons.vfs2.FileSystemConfigBuilder
import org.apache.commons.vfs2.FileSystemOptions
import org.apache.commons.vfs2.provider.url.UrlFileSystem

object ClasspathFileSystemConfigBuilder {
  lazy val instance: ClasspathFileSystemConfigBuilder = new ClasspathFileSystemConfigBuilder

  def setClassLoader(opts: FileSystemOptions, classLoader: ClassLoader) {
    instance.setClassLoader(opts, classLoader)
  }

  def getClassLoader(opts: FileSystemOptions): ClassLoader = {
    instance.getClassLoader(opts)
  }
}

class ClasspathFileSystemConfigBuilder extends FileSystemConfigBuilder("classpath.") {
  private val CLASSLOADER_OPTION_NAME = classOf[ClassLoader].getName

  def setClassLoader(opts: FileSystemOptions, classLoader: ClassLoader) {
    setParam(opts, CLASSLOADER_OPTION_NAME, classLoader)
  }

  def getClassLoader(opts: FileSystemOptions): ClassLoader = {
    getParam(opts, CLASSLOADER_OPTION_NAME).asInstanceOf[ClassLoader]
  }

  protected def getConfigClass: Class[_ <: FileSystem] = classOf[UrlFileSystem]
}