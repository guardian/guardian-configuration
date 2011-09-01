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
import java.util.Properties;
import java.util.Set;

public interface Configuration {

   /**
      * Get an identifier for this configuration. Used for identifying source of property in composite
      * based configurations.
      * @return identifier for this configuration
      */
   public String getIdentifier();

   /**
      * Get the source of a property
      * @param propertyName name of the property
      * @return the source of the property or null if the property is unknown
      */
   public String getPropertySource(String propertyName);

   /**
      * Return true if configuration has a property with the given name
      * @param propertyName name of the property
      * @return true if configuration contains named property
      */
   public boolean hasProperty(String propertyName);

   /**
      * Return a set of property names
      * @return set of property names
      */
   public Set<String> getPropertyNames();

   /**
      * Return the value of property
      * @param propertyName name of the property
      * @return value of the property
      * @throws PropertyNotSetException if property has not been set
      */
   public String getStringProperty(String propertyName) throws PropertyNotSetException;

   /**
      * Return the value of property, or default if property is not set
      * @param propertyName name of the property
      * @param defaultValue value to return if property not set
      * @return value of the property or defaultValue if property not set
      */
   public String getStringProperty(String propertyName, String defaultValue);

   /**
      * Returns the value of a property converted to an int
      * @param propertyName name of the property
      * @return integer value
      * @throws PropertyNotSetException if property has not been set
      * @throws NumberFormatException if the property was found but was not an integer
      */
   public int getIntegerProperty(String propertyName) throws PropertyNotSetException, NumberFormatException;


   /**
      * Returns the value of a property converted to an int, or a default value if property is not found
      * or is not an integer
      * @param propertyName name of the property
      * @param defaultValue value to return if property not set
      * @return alue of the property or defaultValue if property not set or not an integer
      */
   public int getIntegerProperty(String propertyName, int defaultValue);

   /**
      * Return the value of property
      * @param propertyName name of the property
      * @return value of the property
      * @throws PropertyNotSetException if property has not been set
      */
   public List<String> getStringPropertiesSplitByComma(String propertyName) throws PropertyNotSetException;

   /**
      * Return a count of the properties in this configuration
      * @return size of this configuration
      */
   public int size();

   /**
      * Return a properties version of this configuration
      * @return this configuration as a java.util.Properties object
      */
   public Properties toProperties();

   /**
      * Return a projection of this configuration to the given set of properties
      * @param properties the names of the properties to retain in the projection
      * @return this configuration with only the named properties
      */
   public Configuration project(Set<String> properties);

   /**
      * Return a projection of this configuration to the given set of properties
      * @param properties a configuration containing the names of the properties to retain in the projection
      * @return this configuration with only the given properties
      */
   public Configuration project(Configuration properties);

   /**
      * Return a copy of this configuration with the the given set of properties removed
      * @param properties the names of the properties to remove
      * @return this configuration with the named properties removed
      */
   public Configuration minus(Set<String> properties);

   /**
      * Return a copy of this configuration with the the given set of properties removed
      * @param properties a configuration containing the names of the properties to remove
      * @return this configuration with the named properties removed
      */
   public Configuration minus(Configuration properties);

}
