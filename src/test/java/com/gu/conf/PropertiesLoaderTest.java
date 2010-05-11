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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesLoaderTest {

    //private static String INSTALLATION_PROPERTIES = "file:///etc/gu/installation.properties";
    private static String DEV_OVERRIDE_SYS_PROPERTIES =
            String.format("file://%s/.gu/webapp.properties", System.getProperty("user.home"));
    private static String SYS_PROPERTIES = "file:///etc/gu/webapp.properties";
    private static String GLOBAL_PROPERTIES = "classpath:/conf/global.properties";
    private static String ENVIRONMENTAL_PROPERTIES = "classpath:/conf/gudev.gnl.properties";

    @Mock FileAndResourceLoader fileLoader;
    @Mock ServiceDomainProvider serviceDomainProvider;
    PropertiesLoader loader;

    @Before
    public void setUp() throws IOException {
        when(serviceDomainProvider.getServiceDomain()).thenReturn("gudev.gnl");

        // DEV_OVERRIDE_SYS_PROPERTIES
        Properties properties = new PropertiesBuilder()
            .property("source", "dev.override.sys.properties")
            .toProperties();
        when(fileLoader.getPropertiesFrom(DEV_OVERRIDE_SYS_PROPERTIES)).thenReturn(properties);
        when(fileLoader.exists(DEV_OVERRIDE_SYS_PROPERTIES)).thenReturn(true);

        // SYS_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "sys.properties")
            .toProperties();
        when(fileLoader.getPropertiesFrom(SYS_PROPERTIES)).thenReturn(properties);

        // GLOBAL_DEV_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "global.dev.properties")
            .toProperties();
        when(fileLoader.getPropertiesFrom(GLOBAL_PROPERTIES)).thenReturn(properties);

        // ENVIRONMENTAL_DEV_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "env.dev.properties")
            .toProperties();
        when(fileLoader.getPropertiesFrom(ENVIRONMENTAL_PROPERTIES)).thenReturn(properties);

        loader = new PropertiesLoader(fileLoader, serviceDomainProvider);
    }

    @Test
    public void shouldIncludeInstallationPropertiesInConfiguration() throws IOException {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");

        for (PropertiesWithSource properties : propertiesList) {
            assertThat(properties.getStringProperty("source"), not("installation.properties"));
            assertThat(properties.getStringProperty("int.service.domain"), nullValue());
            assertThat(properties.getStringProperty("stage"), nullValue());
        }
    }

    @Test
    public void shouldLoadDevOverrideSysProperties() throws IOException {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, DEV_OVERRIDE_SYS_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("dev.override.sys.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadSystemWebappProperties() throws IOException {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, SYS_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("sys.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadWebappGlobalProperties() throws IOException {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, GLOBAL_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("global.dev.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadWebappStageProperties() throws IOException {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, ENVIRONMENTAL_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("env.dev.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }
    
    private PropertiesWithSource getPropertiesWithSource(List<PropertiesWithSource> propertiesList, String source) {
        for (PropertiesWithSource properties : propertiesList) {
            if (properties.getSource().equals(source)) {
                return properties;
            }
        }

        return null;
    }
}
