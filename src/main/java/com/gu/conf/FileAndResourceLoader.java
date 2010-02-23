package com.gu.conf;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.Properties;

public class FileAndResourceLoader {

    private static final Logger LOG = Logger.getLogger(FileAndResourceLoader.class);

    public boolean exists(String location) {
        return new File(location).exists();
    }

    private InputStream getFile(String filename) throws IOException {
        if (!exists(filename)) {
            LOG.error("File does not exist trying to load properties from " + filename);
            throw new FileNotFoundException(filename);
        }

        return new BufferedInputStream(new FileInputStream(filename));
    }

    private InputStream getResource(String resource) throws IOException {
        ClassLoader classloader = FileAndResourceLoader.class.getClassLoader();
        URL url = classloader.getResource(resource);

        InputStream inputStream;
        try {
             inputStream = url.openStream();
        } catch (IOException ioe) {
            LOG.info("Cannot open resource trying to load properties from " + resource);
            throw ioe;
        }

        return new BufferedInputStream(inputStream);
    }

    public Properties getPropertiesFrom(String descriptor) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            if (descriptor.startsWith("file://")) {
                inputStream = getFile(descriptor.substring(7));
            } else if (descriptor.startsWith("classpath:")) {
                inputStream = getResource(descriptor.substring(10));
            } else {
                LOG.error("Unknown protocol trying to load properties from " + descriptor);
            }

            properties.load(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return properties;
    }
}
