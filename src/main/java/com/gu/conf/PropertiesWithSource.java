package com.gu.conf;

import java.util.Properties;

class PropertiesWithSource {

    private Properties properties;
    private String source;

    PropertiesWithSource(Properties properties, String source) {
        this.properties = properties;
        this.source = source;
    }

    String getStringProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    String getSource() {
        return source;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t");
        stringBuilder.append("# Properties from ");
        stringBuilder.append(source);
        stringBuilder.append("\n");

        for (String propertyName : properties.stringPropertyNames()) {
            stringBuilder.append("\t\t");
            stringBuilder.append(propertyName);
            stringBuilder.append(" : ");
            if (!propertyName.contains("pass")) {
                stringBuilder.append(properties.getProperty(propertyName));
            } else {
                stringBuilder.append("*** PASSWORD ****");
            }
            stringBuilder.append("\n");
        }

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    public PropertiesWithSource getPropertiesActiveInConfiguration(Configuration configuration) {
        Properties activeProperties = new Properties();

        for (String propertyKey : properties.stringPropertyNames()) {
            if (configuration.getPropertySource(propertyKey).equals(source)) {
                activeProperties.setProperty(propertyKey, properties.getProperty(propertyKey));
            }
        }

        return new PropertiesWithSource(activeProperties, source);
    }
}