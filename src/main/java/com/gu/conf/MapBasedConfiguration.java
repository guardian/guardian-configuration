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

import java.util.HashMap;
import java.util.Map;

public class MapBasedConfiguration extends ConfigurationAdaptor {

    private Map<String,String> properties = new HashMap<String,String>();

    /**
     * Add a property value to this MapBasedConfiguration. Inconsistent with property file based implementation,
     * this DOES overwrite previous values
     * @param propertyName name of the property to add
     * @param propertyValue value of the property to add
     */
    public void add(String propertyName, String propertyValue) {
        properties.put(propertyName, propertyValue);
    }


    /**
     * Not applicable for MapBasedConfiguration. Properties do not have sources.
     * @param propertyName name of the property
     * @return the source of the property, always "instance",
     *   or null if the property is unknown
     */
    public String getPropertySource(String propertyName) {
        if (properties.containsKey(propertyName)) {
            return "instance";
        }

        return null;
    }

    /**
     * Return the value of property, or default if property is not set
     * @param propertyName name of the property
     * @param defaultValue value to return if property not set
     * @return value of the property or defaultValue if property not set
     */
    public String getStringProperty(String propertyName, String defaultValue) {
        String value = properties.get(propertyName);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String propertyName : properties.keySet()) {
            String propertyValue = properties.get(propertyName);
            stringBuilder.append(PrinterUtil.propertyString(propertyName, propertyValue));
        }

        return stringBuilder.toString();
    }

}