package com.gu.conf;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class StageResolver {

    private static final String INSTALLATION_PROPERTIES_FILE = "/etc/gu/installation.properties";

    class FileLoader {
        InputStream getFile(String filename) throws IOException {
            return new FileInputStream(filename);
        }
    }

    private FileLoader fileLoader = new FileLoader();
    private Properties stageProperties;

    Properties getStageProperties() {
        if (stageProperties == null) {
            stageProperties = new Properties();

            InputStream inputStream = null;
            try {
                inputStream = fileLoader.getFile(INSTALLATION_PROPERTIES_FILE);
                stageProperties.load(inputStream);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        return stageProperties;
    }

    String getIntServiceDomain() {
        return getStageProperties().getProperty("int.service.domain");
    }

    void setFileLoader(FileLoader fileLoader) {
        this.fileLoader = fileLoader;
    }
}