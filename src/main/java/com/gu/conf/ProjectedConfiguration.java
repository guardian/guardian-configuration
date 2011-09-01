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

import java.util.HashSet;
import java.util.Set;

public class ProjectedConfiguration extends ConfigurationAdaptor {

   private Configuration delegate;
   private Set<String> propertyNames;

   public ProjectedConfiguration(Configuration delegate, Set<String> projection) {
      super(delegate.getIdentifier());
      this.delegate = delegate;

      this.propertyNames = new HashSet<String>();
      this.propertyNames.addAll(projection);
      this.propertyNames.retainAll(delegate.getPropertyNames());
   }

   @Override
   public Configuration getPropertySource(String propertyName) {
      Configuration source = null;
      if (propertyNames.contains(propertyName)) {
         source = delegate.getPropertySource(propertyName);
      }

      return source;
   }

   /**
    * Return the value of property, or default if property is not set
    *
    * @param propertyName name of the property
    * @param defaultValue value to return if property not set
    * @return value of the property or defaultValue if property not set
    */
   public String getStringProperty(String propertyName, String defaultValue) {
      String value = defaultValue;
      if (propertyNames.contains(propertyName)) {
         value = delegate.getStringProperty(propertyName, defaultValue);
      }

      return value;
   }

   @Override
   public Set<String> getPropertyNames() {
      return propertyNames;
   }
}