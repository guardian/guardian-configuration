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

public class PropertiesFileBasedConfiguration extends ConfigurationAdaptor {

   private Properties properties;

   PropertiesFileBasedConfiguration(Properties properties, String source) {
      this.properties = properties;
      setIdentifier(source);
   }

   public String getSource() {
      return getIdentifier();
   }

   @Override
   public String getPropertySource(String propertyName) {
      return hasProperty(propertyName) ? getSource() : null;
   }

   @Override
   public String getStringProperty(String propertyName, String defaultValue) {
      return properties.getProperty(propertyName, defaultValue);
   }

   @Override
   public Set<String> getPropertyNames() {
      return properties.stringPropertyNames();
   }
}