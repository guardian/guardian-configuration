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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;

public class MapBasedConfigurationTest {

    MapBasedConfiguration configuration;

    @Before
    public void setUp() {
        configuration = new MapBasedConfiguration();

        configuration.add("precendence.test.property", "first");
        configuration.add("double.property", "25.0");

        configuration.add("precendence.test.property", "second");
        configuration.add("integer.property", "23");
        configuration.add("nonnumeric.property", "qwe");
        configuration.add("list.property", "rimbaud,verlaine");
    }

    @Test
    public void shouldGetPropertySource() {
        String propertySource = configuration.getPropertySource("nonnumeric.property");
        assertThat(propertySource, is("instance"));
    }

    @Test
    public void shouldGetNullForPropertySourceIfNotSet() {
        String propertySource = configuration.getPropertySource("nosuch.property");
        assertThat(propertySource, nullValue());
    }

    @Test
    public void shouldGetProperty() throws Exception {
        String property = configuration.getStringProperty("nonnumeric.property");
        assertThat(property, is("qwe"));
    }

    @Test
    public void shouldGetFalseForHasPropertyIfNotSet() {
        assertThat(configuration.hasProperty("nosuch.property"), is(false));
    }

    @Test
    public void shouldGetTrueForHasPropertyIfSet() {
        assertThat(configuration.hasProperty("nonnumeric.property"), is(true));
    }

    @Test
    public void shouldThrowForPropertyIfNotSet()  {
        try {
            configuration.getStringProperty("nosuch.property");
            fail("exception expected");
        } catch (PropertyNotSetException ex) {
            assertThat(ex.getProperty(), is("nosuch.property"));
            assertThat(ex.getMessage(), is("Mandatory configuration property 'nosuch.property' was not found."));
        }
    }

    @Test
    public void shouldGetDefaultForPropertyIfNotSet() {
        String property = configuration.getStringProperty("nosuch.property", "default");
        assertThat(property, is("default"));
    }

    @Test
    public void shouldGetIntegerProperty() throws Exception {
        Integer property = configuration.getIntegerProperty("integer.property");
        assertThat(property, is(23));
    }

    @Test
    public void shouldThrowForIntegerPropertyIfNotSet()  {
        try {
            configuration.getIntegerProperty("nosuch.property");
            fail("exception expected");
        } catch (PropertyNotSetException ex) {
            assertThat(ex.getProperty(), is("nosuch.property"));
            assertThat(ex.getMessage(), is("Mandatory configuration property 'nosuch.property' was not found."));
        }
    }

    @Test
    public void shouldThrowForIntegerPropertyIfNotInteger() throws PropertyNotSetException {
        try {
            configuration.getIntegerProperty("double.property");
            fail("exception expected");
        } catch (NumberFormatException ex) {
            //expected
        }

        try {
            configuration.getIntegerProperty("nonnumeric.property");
            fail("exception expected");
        } catch (NumberFormatException ex) {
            //expected
        }
    }

    @Test
    public void shouldGetDefaultForIntegerPropertyIfNotSet() throws IOException {
        Integer property = configuration.getIntegerProperty("nosuch.property", 34);
        assertThat(property, is(34));
    }

    @Test
    public void shouldGetDefaultForIntegerPropertyIfNotInteger() throws IOException {
        Integer property = configuration.getIntegerProperty("double.property", 45);
        assertThat(property, is(45));

        property = configuration.getIntegerProperty("nonnumeric.property", 65);
        assertThat(property, is(65));
    }

    @Test
    public void shouldNotRespectFirstDeclarationPrecedenceInGetProperty() throws Exception {
        String property = configuration.getStringProperty("precendence.test.property");
        assertThat(property, is("second"));
    }

    @Test
    public void shouldGetPropertyList() throws Exception {
        List<String> properties = configuration.getStringPropertiesSplitByComma("list.property");
        assertThat(properties.size(), is(2));
        assertThat(properties.get(0), is("rimbaud"));
        assertThat(properties.get(1), is("verlaine"));
    }

    @Test
    public void shouldGetPropertyNames() throws Exception {
        Set<String> names = configuration.getPropertyNames();
        assertThat(names.size(), is(5));
        assertThat(names, hasItem("precendence.test.property"));
        assertThat(names, hasItem("double.property"));
        assertThat(names, hasItem("integer.property"));
        assertThat(names, hasItem("nonnumeric.property"));
        assertThat(names, hasItem("list.property"));
    }

}