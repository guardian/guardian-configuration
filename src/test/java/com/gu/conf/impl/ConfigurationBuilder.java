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

class ConfigurationBuilder {
   private String identifier = "ConfigurationBuilder";
   private Map<String, String> properties = new HashMap<String, String>();

   public AbstractConfiguration toConfiguration() {
      MapBasedConfiguration configuration = new MapBasedConfiguration(identifier);
      for (String key: properties.keySet()) {
         configuration.add(key, properties.get(key));
      }

      return configuration;
   }

   public ConfigurationBuilder property(String key, String value) {
      properties.put(key, value);
      return this;
   }

   public ConfigurationBuilder identifier(String identifier) {
      this.identifier = identifier;
      return this;
   }
}