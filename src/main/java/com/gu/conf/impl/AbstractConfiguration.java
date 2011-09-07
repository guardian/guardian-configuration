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

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

import com.gu.conf.Configuration;
import com.gu.conf.PropertyNotSetException;

import java.util.*;

abstract class AbstractConfiguration implements Configuration {

   private String identifier;

   AbstractConfiguration(String identifier) {
      this.identifier = identifier;
   }

   @Override
   public String getIdentifier() {
      return identifier;
   }

   @Override
   public boolean hasProperty(String propertyName) {
      return getPropertyNames().contains(propertyName);
   }

   @Override
   public String getStringProperty(String propertyName) throws PropertyNotSetException {
      String value = getStringProperty(propertyName, null);
      if (value == null) {
         throw new PropertyNotSetException(propertyName);
      }

      return value;
   }

   @Override
   public int getIntegerProperty(String propertyName) throws PropertyNotSetException, NumberFormatException {
      return parseInt(getStringProperty(propertyName));
   }

   @Override
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

   @Override
   public List<String> getStringPropertiesSplitByComma(String propertyName) throws PropertyNotSetException {
      return asList(getStringProperty(propertyName).split(","));
   }

   @Override
   public Properties toProperties() {
      Properties properties = new Properties();
      for (String property : getPropertyNames()) {
         properties.setProperty(property, getStringProperty(property, null));
      }

      return properties;
   }

   /**
    * Get the source of a property
    *
    * @param propertyName name of the property
    * @return the source of the property or null if the property is unknown
    */
   abstract AbstractConfiguration getPropertySource(String propertyName);

   /**
    * Return a count of the properties in this configuration
    *
    * @return size of this configuration
    */
   int size() {
      return getPropertyNames().size();
   }

   /**
    * Return a projection of this configuration to the given set of properties
    *
    * @param properties the names of the properties to retain in the projection
    * @return this configuration with only the named properties
    */
   AbstractConfiguration project(Set<String> properties) {
      return new ProjectedConfiguration(this, properties);
   }

   /**
    * Return a projection of this configuration to the given set of properties
    *
    * @param properties a configuration containing the names of the properties to retain in the projection
    * @return this configuration with only the given properties
    */
   AbstractConfiguration project(AbstractConfiguration properties) {
      return project(properties.getPropertyNames());
   }

   /**
    * Return a copy of this configuration with the the given set of properties removed
    *
    * @param properties the names of the properties to remove
    * @return this configuration with the named properties removed
    */
   AbstractConfiguration minus(Set<String> properties) {
      Set<String> retain = new HashSet<String>();
      retain.addAll(this.getPropertyNames());
      retain.removeAll(properties);

      return new ProjectedConfiguration(this, retain);
   }

   /**
    * Return a copy of this configuration with the the given set of properties removed
    *
    * @param properties a configuration containing the names of the properties to remove
    * @return this configuration with the named properties removed
    */
   AbstractConfiguration minus(AbstractConfiguration properties) {
      return minus(properties.getPropertyNames());
   }

   /**
    * Return a configuration with the same property names as this configuration but with
    * property values overriden with values from the given configuration.
    *
    * @param overrides a configuration containing the property override values. Properties
    *                  not in the base configuration are ignore.
    * @return this configuration with the given overrides.
    */
   AbstractConfiguration overrideWith(AbstractConfiguration overrides) {
      return new CompositeConfiguration(overrides.project(this), this);
   }

   @Override
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