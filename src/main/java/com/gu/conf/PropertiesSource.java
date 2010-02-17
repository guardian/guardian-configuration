package com.gu.conf;


public enum PropertiesSource {

    // List from lowest precendence to highest, so compareTo relation is same as precendence
    WEBAPP_STAGE_PROPERTIES("Webapp Stage Specific Configuration Properties"),
    WEBAPP_GLOBAL_PROPERTIES("Webapp Global Configuration Properties"),
    SYSTEM_WEBAPP_PROPERTIES("/etc/gu/ Webapp Systems Configuration Properties"),
    DEV_SYSTEM_WEBAPP_PROPERTIES("~/etc/gu/ DEV Webapp Systems Configuration Properties");

    private String description;

    private PropertiesSource(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}