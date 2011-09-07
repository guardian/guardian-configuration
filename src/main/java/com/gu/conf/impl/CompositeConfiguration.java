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

import java.util.*;

class CompositeConfiguration extends AbstractConfiguration {

   private AbstractConfiguration primary;
   private AbstractConfiguration secondary;

   CompositeConfiguration(AbstractConfiguration primary, AbstractConfiguration secondary) {
      super(String.format("composite[%s//%s]", primary.getIdentifier(), secondary.getIdentifier()));
      this.primary = primary;
      this.secondary = secondary.minus(primary);
   }

   static AbstractConfiguration from(AbstractConfiguration... confs) {
      return from(new LinkedList<AbstractConfiguration>(Arrays.asList(confs)));
   }

   private static AbstractConfiguration from(List<AbstractConfiguration> confs) {
      if (confs.size() == 1) {
         return confs.get(0);
      }

      AbstractConfiguration head = confs.remove(0);
      AbstractConfiguration tail = from(confs);

      return new CompositeConfiguration(head, tail);
   }

   @Override
   AbstractConfiguration getPropertySource(String propertyName) {
      AbstractConfiguration source = primary.getPropertySource(propertyName);
      if (source == null) {
         source = secondary.getPropertySource(propertyName);
      }

      return source;
   }

   @Override
   public String getStringProperty(String propertyName, String defaultValue) {
      String value = primary.getStringProperty(propertyName, null);
      if (value == null) {
         value = secondary.getStringProperty(propertyName, defaultValue);
      }

      return value;
   }

   @Override
   public Set<String> getPropertyNames() {
      Set<String> propertyNames = new HashSet<String>();
      propertyNames.addAll(primary.getPropertyNames());
      propertyNames.addAll(secondary.getPropertyNames());

      return propertyNames;
   }

   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(primary.toString());
      stringBuilder.append(secondary.toString());

      return stringBuilder.toString();
   }
}