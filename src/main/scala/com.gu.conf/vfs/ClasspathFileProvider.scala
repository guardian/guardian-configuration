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

import org.apache.commons.vfs2.provider.AbstractFileProvider
import org.apache.commons.vfs2.provider.UriParser

import scala.collection.JavaConversions._
import org.apache.commons.vfs2._

class ClasspathFileProvider extends AbstractFileProvider {

  def findFile(baseFile: FileObject, uri: String, options: FileSystemOptions): FileObject = {
    val remaining = new java.lang.StringBuilder
    UriParser.extractScheme(uri, remaining)

    val resource = remaining.toString

    val classloader = Option(ClasspathFileSystemConfigBuilder getClassLoader options) getOrElse {
      getClass.getClassLoader
    }

    val url = Option(classloader getResource resource) map { _.toExternalForm }

    url map { getContext.getFileSystemManager.resolveFile } getOrElse null
  }

  override def getConfigBuilder: FileSystemConfigBuilder = {
    ClasspathFileSystemConfigBuilder.instance
  }

  override def closeFileSystem(filesystem: FileSystem) {
  }

  def getCapabilities: java.util.Collection[Capability] = Set(Capability.DISPATCHER)
}