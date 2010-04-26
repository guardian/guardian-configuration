package com.gu.conf.exceptions;

public class PropertyNotSetException extends Exception {
    private final String property;

    public PropertyNotSetException(String property, String sources) {
        super("Mandatory configuration property '"+ property +"' was not found in any of "+ sources);
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
