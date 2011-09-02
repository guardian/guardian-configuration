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

public class ConfigurationProxy implements Configuration {

   private Configuration delegate;

   protected ConfigurationProxy() { }

   public ConfigurationProxy(Configuration delegate) {
      this.delegate = delegate;
   }

   protected void setDelegate(Configuration delegate) {
      this.delegate = delegate;
   }

   @Override
   public String getIdentifier() {
      return delegate.getIdentifier();
   }

   @Override
   public Configuration getPropertySource(String propertyName) {
      return delegate.getPropertySource(propertyName);
   }

   @Override
   public boolean hasProperty(String propertyName) {
      return delegate.hasProperty(propertyName);
   }

   @Override
   public String getStringProperty(String propertyName, String defaultValue) {
      return delegate.getStringProperty(propertyName, defaultValue);
   }

   @Override
   public int getIntegerProperty(String propertyName) throws PropertyNotSetException, NumberFormatException {
      return delegate.getIntegerProperty(propertyName);
   }

   @Override
   public int getIntegerProperty(String propertyName, int defaultValue) {
      return delegate.getIntegerProperty(propertyName, defaultValue);
   }

   @Override
   public List<String> getStringPropertiesSplitByComma(String propertyName) throws PropertyNotSetException {
      return delegate.getStringPropertiesSplitByComma(propertyName);
   }

   @Override
   public int size() {
      return delegate.size();
   }

   @Override
   public Properties toProperties() {
      return delegate.toProperties();
   }

   @Override
   public Configuration project(Set<String> properties) {
      return delegate.project(properties);
   }

   @Override
   public Configuration project(Configuration properties) {
      return delegate.project(properties);
   }

   @Override
   public Configuration minus(Set<String> properties) {
      return delegate.minus(properties);
   }

   @Override
   public Configuration minus(Configuration properties) {
      return delegate.minus(properties);
   }

   @Override
   public Configuration overrideWith(Configuration overrides) {
      return delegate.overrideWith(overrides);
   }

   @Override
   public Set<String> getPropertyNames() {
      return delegate.getPropertyNames();
   }

   @Override
   public String getStringProperty(String propertyName) throws PropertyNotSetException {
      return delegate.getStringProperty(propertyName);
   }

   @Override
   public String toString() {
      return delegate.toString();
   }
}