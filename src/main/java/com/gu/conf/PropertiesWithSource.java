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

import java.util.Properties;
import java.util.Set;

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

    public Set<Object> propertyKeys() {
        return properties.keySet();
    }

    String getSource() {
        return source;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("# Properties from ");
        stringBuilder.append(source);
        stringBuilder.append("\n");

        for (String propertyName : properties.stringPropertyNames()) {
            String propertyValue = properties.getProperty(propertyName);
            stringBuilder.append(PrinterUtil.propertyString(propertyName, propertyValue));
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