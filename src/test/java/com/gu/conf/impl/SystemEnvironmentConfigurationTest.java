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

import org.junit.After;
import org.junit.Before;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SystemEnvironmentConfigurationTest extends AbstractConfigurationTestBase {

   private Map<String, String> oldEnvironment;

   @Before
   public void setUp() throws Exception {
      oldEnvironment = System.getenv();

      Map<String, String> environment = new HashMap<String, String>();
      environment.put("double.property", "25.0");
      environment.put("precendence.test.property", "second");
      environment.put("integer.property", "23");
      environment.put("nonnumeric.property", "qwe");
      environment.put("list.property", "rimbaud,verlaine");
      environment.put("utility.property", "utility");

      setEnvironment(environment);

      configuration = new SystemEnvironmentConfiguration();
   }

   /*
    * Hack to set the environment properties. This gets the Map<String,String> that the JVM gives
    * us from a System.getenv() and uses the fact that this is an UnmodifiableMap wrapper
    * around the real environment map which is mutable and can be cracked with reflection.
    *
    *  See:
    *    http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java/496849#496849
    */
   private void setEnvironment(Map<String, String> environment) throws Exception {
      Map<String, String> actualEnv = System.getenv();

      for (Class clazz : Collections.class.getDeclaredClasses()) {
         if ("java.util.Collections$UnmodifiableMap".equals(clazz.getName())) {
            Field backingCollectionfield = clazz.getDeclaredField("m");
            backingCollectionfield.setAccessible(true);
            Map<String, String> modifiableEnv = (Map<String, String>) backingCollectionfield.get(actualEnv);

            modifiableEnv.clear();
            modifiableEnv.putAll(environment);
         }
      }
   }

   @After
   public void tearDown() throws Exception {
      setEnvironment(oldEnvironment);
   }

}