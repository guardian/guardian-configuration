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


import com.gu.conf.impl.GuardianConfigurationStrategy;
import java.io.IOException;

public class ConfigurationFactory {

   public Configuration getConfiguration(String applicationName) throws IOException {
      return getConfiguration(applicationName, "conf");
   }

   public Configuration getConfiguration(String applicationName, String webappConfDirectory) throws IOException {
      GuardianConfigurationStrategy strategy = new GuardianConfigurationStrategy();
      return strategy.getConfiguration(applicationName, webappConfDirectory);
   }
}
