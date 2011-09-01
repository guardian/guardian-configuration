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

package com.gu.conf;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

import com.gu.conf.exceptions.PropertyNotSetException;

import java.util.*;

abstract class ConfigurationAdaptor implements Configuration {

    private String identifier;

    protected ConfigurationAdaptor(String identifier) {
       this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
       return identifier;
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return getStringProperty(propertyName, null) != null;
    }

    /**
     * Return the value of property
     * @param propertyName name of the property
     * @return value of the property
     * @throws com.gu.conf.exceptions.PropertyNotSetException if property has not been set
     */
    public String getStringProperty(String propertyName) throws PropertyNotSetException {
        String value = getStringProperty(propertyName, null);
        if (value == null) {
            throw new PropertyNotSetException(propertyName);
        }

        return value;
    }

    /**
     * Returns the value of a property converted to an int
     * @param propertyName name of the property
     * @return integer value
     * @throws com.gu.conf.exceptions.PropertyNotSetException if property has not been set
     * @throws NumberFormatException if the property was found but was not an integer
     * @throws com.gu.conf.exceptions.PropertyNotSetException if property has not been set
     */
    public int getIntegerProperty(String propertyName) throws PropertyNotSetException, NumberFormatException {
        return parseInt(getStringProperty(propertyName));
    }


    /**
     * Returns the value of a property converted to an int, or a default value if property is not found
     * or is not an integer
     * @param propertyName name of the property
     * @param defaultValue value to return if property not set
     * @return value of the property or defaultValue if property not set or not an integer
     */
    public int getIntegerProperty(String propertyName, int defaultValue) {
        int property = defaultValue;
        try {
            property = parseInt(getStringProperty(propertyName));
        } catch (NumberFormatException nfe) {
            // ignore
        } catch (PropertyNotSetException e) {
            // ignore
        }

        return property;
    }

    /**
     * Return the value of property
     * @param propertyName name of the property
     * @return value of the property
     * @throws com.gu.conf.exceptions.PropertyNotSetException if property has not been set
     */
    public List<String> getStringPropertiesSplitByComma(String propertyName) throws PropertyNotSetException {
       return asList(getStringProperty(propertyName).split(","));
    }

    /**
       * Return a count of the properties in this configuration
       * @return size of this configuration
       */
    public int size() {
       return getPropertyNames().size();
    }

    /**
       * Return a properties version of this configuration
       * @return this configuration as a java.util.Properties object
       */
    public Properties toProperties() {
       Properties properties = new Properties();
       for (String property: getPropertyNames()) {
          properties.setProperty(property, getStringProperty(property, null));
       }

       return properties;
    }

    /**
       * Return a projection of this configuration to the given set of properties
       * @param properties the names of the properties to retain in the projection
       * @return this configuration with only the named properties
       */
    public Configuration project(Set<String> properties) {
       return new ProjectedConfiguration(this, properties);
    }

    /**
       * Return a projection of this configuration to the given set of properties
       * @param properties a configuration containing the names of the properties to retain in the projection
       * @return this configuration with only the given properties
       */
    public Configuration project(Configuration properties) {
       return project(properties.getPropertyNames());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("# Properties from ");
        stringBuilder.append(getIdentifier());
        stringBuilder.append("\n");

        List<String> propertyNames = new ArrayList<String>(getPropertyNames());
        Collections.sort(propertyNames);

        for (String propertyName : propertyNames) {
            String propertyValue = getStringProperty(propertyName, "");
            stringBuilder.append(PrinterUtil.propertyString(propertyName, propertyValue));
        }

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}