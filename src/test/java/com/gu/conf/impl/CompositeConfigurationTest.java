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
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CompositeConfigurationTest extends AbstractConfigurationTestBase {

   private Configuration secondary;

   @Before
   public void setUp() {
      MapBasedConfiguration conf1 = new MapBasedConfiguration("primary");
      conf1.add("precendence.test.property", "first");
      conf1.add("double.property", "25.0");


      MapBasedConfiguration conf2 = new MapBasedConfiguration("secondary");
      conf2.add("precendence.test.property", "second");
      conf2.add("integer.property", "23");
      conf2.add("nonnumeric.property", "qwe");
      conf2.add("list.property", "rimbaud,verlaine");
      conf2.add("utility.property", "utility");

      configuration = new CompositeConfiguration(conf1, conf2);
      secondary = conf2;
   }

   @Test
   public void shouldGetPropertySource() {
      Configuration propertySource = configuration.getPropertySource("nonnumeric.property");
      assertThat(propertySource, is(secondary));
   }

   @Test
   public void shouldGivePrecedenceToFirstConfiguration() throws Exception {
      String property = configuration.getStringProperty("precendence.test.property");
      assertThat(property, is("first"));
   }

   @Test
   public void shouldToStringInStandardFormat() {
      String expected =
         "# Properties from primary\n" +
            "double.property=25.0\n" +
            "precendence.test.property=first\n" +
            "\n" +
            "# Properties from secondary\n" +
            "integer.property=23\n" +
            "list.property=rimbaud,verlaine\n" +
            "nonnumeric.property=qwe\n" +
            "utility.property=utility\n" +
            "\n";

      assertThat(configuration.toString(), is(expected));
   }

   @Test
   public void shouldFactoryCompositeConfigurations() throws PropertyNotSetException {
      MapBasedConfiguration conf1 = new MapBasedConfiguration("conf1");
      conf1.add("conf1.property", "conf1");

      MapBasedConfiguration conf2 = new MapBasedConfiguration("conf2");
      conf2.add("conf1.property", "conf2");
      conf2.add("conf2.property", "conf2");

      MapBasedConfiguration conf3 = new MapBasedConfiguration("conf3");
      conf3.add("conf1.property", "conf3");
      conf3.add("conf2.property", "conf3");
      conf3.add("conf3.property", "conf3");

      MapBasedConfiguration conf4 = new MapBasedConfiguration("conf4");
      conf4.add("conf1.property", "conf4");
      conf4.add("conf2.property", "conf4");
      conf4.add("conf3.property", "conf4");
      conf4.add("conf4.property", "conf4");


      AbstractConfiguration configuration = CompositeConfiguration.from(conf1, conf2, conf3, conf4);

      assertThat(configuration.size(), is(4));
      assertThat(configuration.getStringProperty("conf1.property"), is("conf1"));
      assertThat(configuration.getPropertySource("conf1.property"), is((Configuration) conf1));
      assertThat(configuration.getStringProperty("conf2.property"), is("conf2"));
      assertThat(configuration.getPropertySource("conf2.property"), is((Configuration) conf2));
      assertThat(configuration.getStringProperty("conf3.property"), is("conf3"));
      assertThat(configuration.getPropertySource("conf3.property"), is((Configuration) conf3));
      assertThat(configuration.getStringProperty("conf4.property"), is("conf4"));
      assertThat(configuration.getPropertySource("conf4.property"), is((Configuration) conf4));
   }
}