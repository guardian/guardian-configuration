package com.gu.conf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationFactoryTest {

    @Mock StageResolver resolver;
    @Mock ResourceLoader resourceLoader;
    ConfigurationFactory factory;

    @Before
    public void setUp() throws IOException {
        factory = new ConfigurationFactory();
        factory.setStageResolver(resolver);
        factory.setResourceLoader(resourceLoader);

        when(resolver.getIntServiceDomain()).thenReturn("domain.gnl");

        String properties = "property=theft";
        InputStream inputStream = new ByteArrayInputStream(properties.getBytes("UTF-8"));
        when(resourceLoader.getResource(anyString())).thenReturn(inputStream);
    }

    @Test
    public void shouldGetDefaultConfigurationFromConf() throws IOException {
        Configuration configuration = factory.getConfiguration();

        assertThat(configuration, notNullValue());
        verify(resourceLoader).getResource("conf/domain.gnl.properties");
    }

    @Test
    public void shouldGetConfigurationWithSpecifiedPrefix() throws IOException {
        Configuration configuration = factory.getConfiguration("prefix");

        assertThat(configuration, notNullValue());
        verify(resourceLoader).getResource("prefix/domain.gnl.properties");
    }

    @Test
    public void shouldLoadProperties() throws IOException {
        Configuration configuration = factory.getConfiguration();

        assertThat(configuration.getProperty("property"), is("theft"));
    }
}