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

import java.util.Set;

public class ProxiedConfiguration extends ConfigurationAdaptor {

   private Configuration delegate;

   public ProxiedConfiguration(String identifier) {
      super(identifier);
   }

   public ProxiedConfiguration(Configuration delegate) {
      this(delegate.getIdentifier());
      this.delegate = delegate;
   }

   protected void setDelegate(Configuration delegate) {
      this.delegate = delegate;
   }

   @Override
   public Configuration getPropertySource(String propertyName) {
      return delegate.getPropertySource(propertyName);
   }

   @Override
   public String getStringProperty(String propertyName, String defaultValue) {
      return delegate.getStringProperty(propertyName, defaultValue);
   }

   @Override
   public Set<String> getPropertyNames() {
      return delegate.getPropertyNames();
   }
}