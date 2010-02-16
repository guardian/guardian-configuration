package com.gu.conf;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

class PropertiesLoader {

    private static final String INSTALLATION_PROPERTIES_FILE = "/etc/gu/installation.properties";

    private FileAndResourceLoader loader = new FileAndResourceLoader();
    private PropertiesWithSource installationProperties;

    PropertiesLoader() {
        this(new FileAndResourceLoader());
    }

    PropertiesLoader(FileAndResourceLoader loader) {
        this.loader = loader;
    }

    String getIntServiceDomain() {
        return getInstallationProperties().getStringProperty("int.service.domain");
    }

    String getStage() {
        return getInstallationProperties().getStringProperty("stage");
    }

    List<PropertiesWithSource> getProperties(String applicationName, String webappConfDirectory) {
        List<PropertiesWithSource> properties = new LinkedList<PropertiesWithSource>();

        PropertiesWithSource installationProperties = getInstallationProperties();
        properties.add(installationProperties);
//        if (installationProperties.getStringProperty("STAGE").equals("DEV")) {
//           properties.add(getDevOverrideSystemWebappProperties(applicationName));
//        }
        properties.add(getSystemWebappProperties(applicationName));
        properties.add(getWebappGlobalProperties(webappConfDirectory));
        properties.add(getWebappStageProperties(webappConfDirectory));

        return properties;
    }

    private PropertiesWithSource getInstallationProperties() {
        if (installationProperties == null) {
            Properties properties = loader.getPropertiesFromFile(INSTALLATION_PROPERTIES_FILE);
            installationProperties = new PropertiesWithSource(properties, PropertiesSource.INSTALLATION_PROPERTIES);
        }

        return installationProperties;
    }

    private PropertiesWithSource getSystemWebappProperties(String applicationName) {
        String propertiesFile = String.format("/etc/gu/%s.properties", applicationName);
        Properties properties = loader.getPropertiesFromFile(propertiesFile);

        return new PropertiesWithSource(properties, PropertiesSource.SYSTEM_WEBAPP_PROPERTIES);
    }

    private PropertiesWithSource getWebappGlobalProperties(String webappConfDirectory) {
        String propertiesResource = String.format("%s/global.properties", webappConfDirectory);
        Properties properties = loader.getPropertiesFromResource(propertiesResource);

        return new PropertiesWithSource(properties, PropertiesSource.WEBAPP_GLOBAL_PROPERTIES);
    }

    private PropertiesWithSource getWebappStageProperties(String webappConfDirectory) {
        String propertiesResource = String.format("%s/%s.properties", webappConfDirectory, getIntServiceDomain());
        Properties properties = loader.getPropertiesFromResource(propertiesResource);

        return new PropertiesWithSource(properties, PropertiesSource.WEBAPP_STAGE_PROPERTIES);
    }
}