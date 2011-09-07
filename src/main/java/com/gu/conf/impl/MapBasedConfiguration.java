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

package com.gu.conf.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class MapBasedConfiguration extends AbstractConfiguration {

   private Map<String, String> properties;

   MapBasedConfiguration(String identifier) {
      this(identifier, new HashMap<String, String>());
   }

   MapBasedConfiguration(String identifier, Map<String, String> properties) {
      super(identifier);
      this.properties = properties;
   }

   /**
    * Add a property value to this MapBasedConfiguration. Inconsistent with property file based implementation,
    * this DOES overwrite previous values
    *
    * @param propertyName  name of the property to add
    * @param propertyValue value of the property to add
    */
   void add(String propertyName, String propertyValue) {
      properties.put(propertyName, propertyValue);
   }

   @Override
   public AbstractConfiguration getPropertySource(String propertyName) {
      return properties.containsKey(propertyName) ? this : null;
   }

   @Override
   public String getStringProperty(String propertyName, String defaultValue) {
      String value = properties.get(propertyName);
      if (value == null) {
         value = defaultValue;
      }

      return value;
   }

   @Override
   public Set<String> getPropertyNames() {
      return properties.keySet();
   }
}