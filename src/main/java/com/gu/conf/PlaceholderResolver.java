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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderResolver {

   private static final Pattern placeholderRegex = Pattern.compile("(env\\.)?(.*)");

   private SystemEnvironmentConfiguration environment;
   private SystemPropertiesConfiguration system;

   public PlaceholderResolver() {
      this(new SystemEnvironmentConfiguration(), new SystemPropertiesConfiguration());
   }

   PlaceholderResolver(SystemEnvironmentConfiguration environmentConfiguration,
                       SystemPropertiesConfiguration systemPropertiesConfiguration) {
      this.environment = environmentConfiguration;
      this.system = systemPropertiesConfiguration;
   }

   public String resolvePlaceholder(String placeholder) throws PropertyNotSetException {
      Matcher matcher = placeholderRegex.matcher(placeholder);
      matcher.matches();

      if (matcher.group(1) == null) {
         return system.getStringProperty(matcher.group(2));
      } else {
         return environment.getStringProperty(matcher.group(2));
      }
   }
}
