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

import com.gu.conf.Configuration;
import com.gu.conf.PropertyNotSetException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PlaceholderResolver {

   private Pattern PLACEHOLDER = Pattern.compile("\\$\\{(.*?)\\}");
   private static final Pattern PLACEHOLDER_NAME = Pattern.compile("(env\\.)?(.*)");

   private SystemEnvironmentConfiguration environment;
   private SystemPropertiesConfiguration system;

   PlaceholderResolver() {
      this(new SystemEnvironmentConfiguration(), new SystemPropertiesConfiguration());
   }

   PlaceholderResolver(SystemEnvironmentConfiguration environmentConfiguration,
                       SystemPropertiesConfiguration systemPropertiesConfiguration) {
      this.environment = environmentConfiguration;
      this.system = systemPropertiesConfiguration;
   }

   String substitutePlaceholders(String text) {
      StringBuffer substituted = new StringBuffer();

      Matcher matcher = PLACEHOLDER.matcher(text);
      while (matcher.find()) {
         matcher.appendReplacement(substituted, resolvePlaceholder(matcher.group(1)));
      }
      matcher.appendTail(substituted);

      return substituted.toString();
   }

   private String resolvePlaceholder(String placeholder) {
      Matcher matcher = PLACEHOLDER_NAME.matcher(placeholder);
      matcher.matches();

      String property = matcher.group(2);
      Configuration substitutionType = matcher.group(1) == null ? system : environment;

      String substition;
      try {
         substition = substitutionType.getStringProperty(property);
      } catch (PropertyNotSetException e) {
         // Suppress the substitution
         substition = String.format("${%s}", placeholder);
      }

      return substition;
   }
}
