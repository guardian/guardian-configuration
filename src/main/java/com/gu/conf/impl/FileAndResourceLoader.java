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

package com.gu.conf.impl;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileAndResourceLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FileAndResourceLoader.class);
    private static final Pattern protocolMatcher = Pattern.compile("(?:file|classpath):(?://)?(.*)");

    private String stripProtocol(String location) {
        Matcher match = protocolMatcher.matcher(location);
        if (match.matches()) {
            return match.group(1);
        }

        return location;
    }

    private InputStream getFile(String resource) throws IOException {
        File file = new File(stripProtocol(resource));
        if (!file.canRead()) {
            LOG.info("Ignoring missing configuration file " + resource);
            return null;
        }
        return new BufferedInputStream(new FileInputStream(file));
    }

    private InputStream getResource(String resource) {
        ClassLoader classloader = FileAndResourceLoader.class.getClassLoader();
        URL url = classloader.getResource(stripProtocol(resource));

        if (url == null) {
            LOG.info("Ignoring missing configuration file " + resource);
            return null;
        }

        InputStream inputStream;
        try {
             inputStream = url.openStream();
        } catch (IOException ioe) {
            LOG.warn("Cannot open resource trying to load properties from " + resource, ioe);
            return null;
        }

        return new BufferedInputStream(inputStream);
    }

    AbstractConfiguration getConfigurationFrom(String descriptor) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            if (descriptor.startsWith("file:")) {
                inputStream = getFile(descriptor);
            } else if (descriptor.startsWith("classpath:")) {
                inputStream = getResource(descriptor);
            } else {
                throw new RuntimeException("Unknown protocol trying to load properties from " + descriptor);
            }

            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ioe) {
            LOG.warn("unexpected error reading from file " + descriptor, ioe);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return new PropertiesFileBasedConfiguration(descriptor, properties);
    }
}
