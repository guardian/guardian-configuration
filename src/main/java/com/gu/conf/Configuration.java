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

import com.gu.conf.exceptions.PropertyNotSetException;

import java.util.List;

public class Configuration {

    private List<PropertiesWithSource> properties;
    private String sources;

    Configuration(List<PropertiesWithSource> properties) {
        this.properties = properties;
        this.sources = buildSourcesString(properties);
    }

    private String buildSourcesString(List<PropertiesWithSource> propertiesWithSources) {
        StringBuilder s = new StringBuilder();
        for (PropertiesWithSource propertiesWithSource : propertiesWithSources) {
            s.append(propertiesWithSource.getSource());
            s.append(" ");
        }
        return s.toString();
    }

    /**
     * Get the source of a property
     * @param propertyName name of the property
     * @return the source of the property, e.g. classpath:/WEB-INF/conf/blah.properties,
     *   or null if the property is unknown
     */
    public String getPropertySource(String propertyName) {
        for (PropertiesWithSource props : properties) {
            if (props.getStringProperty(propertyName) != null) {
                return props.getSource();
            }
        }

        return null;
    }


    /**
     * Return the value of property
     * @param propertyName name of the property
     * @return value of the property
     * @throws PropertyNotSetException if property has not been set
     */
    public String getStringProperty(String propertyName) throws PropertyNotSetException {
        String value = getStringProperty(propertyName, null);

        if (value == null) {
            throw new PropertyNotSetException(propertyName, sources);
        }

        return value;
    }

    /**
     * Return the value of property, or default if property is not set
     * @param propertyName name of the property
     * @param defaultValue value to return if property not set
     * @return value of the property or defaultValue if property not set
     */
    public String getStringProperty(String propertyName, String defaultValue) {
        for (PropertiesWithSource props : properties) {
            String property = props.getStringProperty(propertyName);
            if (property != null) {
                return property;
            }
        }

        return defaultValue;
    }

    /**
     * Returns the value of a property converted to an int
     * @param propertyName name of the property
     * @return integer value
     * @throws PropertyNotSetException if property has not been set
     * @throws NumberFormatException if the property was found but was not an integer
     */
    public int getIntegerProperty(String propertyName) throws PropertyNotSetException, NumberFormatException {
        return Integer.parseInt(getStringProperty(propertyName));
    }


    /**
     * Returns the value of a property converted to an int, or a default value if property is not found
     * or is not an integer
     * @param propertyName name of the property
     * @param defaultValue value to return if property not set
     * @return alue of the property or defaultValue if property not set or not an integer
     */
    public int getIntegerProperty(String propertyName, int defaultValue) {
        int property = defaultValue;
        try {
            property = Integer.parseInt(getStringProperty(propertyName));
        } catch (NumberFormatException nfe) {
            // ignore
        } catch (PropertyNotSetException e) {
            // ignore
        }

        return property;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (PropertiesWithSource props : properties) {
            PropertiesWithSource activeProperties = props.getPropertiesActiveInConfiguration(this);
            stringBuilder.append(activeProperties);
        }

        return stringBuilder.toString();
    }
}