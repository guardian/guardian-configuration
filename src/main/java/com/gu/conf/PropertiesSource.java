package com.gu.conf;


public enum PropertiesSource {

    // List from lowest precendence to highest, so compareTo relation is same as precendence
    WEBAPP_STAGE_PROPERTIES("Webapp Stage Specific Configuration Properties File"),
    WEBAPP_GLOBAL_PROPERTIES("Webapp Global Configuration Properties File"),
    SYSTEM_WEBAPP_PROPERTIES("/etc/gu/ Webapp Systems Configuration Properties File"),
    DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES("~/etc/gu/ Override Webapp Systems Configuration Properties File"),
    INSTALLATION_PROPERTIES("/etc/gu/installations.properties File");

    private String description;

    private PropertiesSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}