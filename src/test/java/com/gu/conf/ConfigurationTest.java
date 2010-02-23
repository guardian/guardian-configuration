package com.gu.conf;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ConfigurationTest {

    Configuration configuration;

    @Before
    public void setUp() {
        PropertiesBuilder sysProperties = new PropertiesBuilder();
        sysProperties.property("precendence.test.property", "first");
        sysProperties.property("double.property", "25.0");
        sysProperties.source("file:///sys.properties");

        PropertiesBuilder environmentalProperties = new PropertiesBuilder();
        environmentalProperties.property("precendence.test.property", "second");
        environmentalProperties.property("integer.property", "23");
        environmentalProperties.property("nonnumeric.property", "qwe");
        environmentalProperties.source("classpath:///env.dev.properties");

        ImmutableList<PropertiesWithSource> properties = ImmutableList.of(
                sysProperties.toPropertiesWithSource(),
                environmentalProperties.toPropertiesWithSource()
        );

        configuration = new Configuration(properties);
    }

    @Test
    public void shouldGetPropertySource() throws IOException {
        String propertySource = configuration.getPropertySource("nonnumeric.property");
        assertThat(propertySource, is("classpath:///env.dev.properties"));
    }

    @Test
    public void shouldGetNullForPropertySourceIfNotSet() throws IOException {
        String propertySource = configuration.getPropertySource("nosuch.property");
        assertThat(propertySource, nullValue());
    }

    @Test
    public void shouldGetProperty() throws IOException {
        String property = configuration.getStringProperty("nonnumeric.property");
        assertThat(property, is("qwe"));
    }

    @Test
    public void shouldGetNullForPropertyIfNotSet() throws IOException {
        String property = configuration.getStringProperty("nosuch.property");
        assertThat(property, nullValue());
    }

    @Test
    public void shouldGetDefaultForPropertyIfNotSet() throws IOException {
        String property = configuration.getStringProperty("nosuch.property", "default");
        assertThat(property, is("default"));
    }

    @Test
    public void shouldGetIntegerProperty() throws IOException {
        Integer property = configuration.getIntegerProperty("integer.property");
        assertThat(property, is(23));
    }

    @Test
    public void shouldGetNullForIntegerPropertyIfNotSet() throws IOException {
        Integer property = configuration.getIntegerProperty("nosuch.property");
        assertThat(property, nullValue());
    }

    @Test
    public void shouldGetNullForIntegerPropertyIfNotInteger() throws IOException {
        Integer property = configuration.getIntegerProperty("double.property");
        assertThat(property, nullValue());

        property = configuration.getIntegerProperty("nonnumeric.property");
        assertThat(property, nullValue());
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
    public void shouldRespectFirstDeclarationPrecedenceInGetPropertySource() throws IOException {
        String propertySource = configuration.getPropertySource("precendence.test.property");
        assertThat(propertySource, is("file:///sys.properties"));
    }

    @Test
    public void shouldRespectFirstDeclarationPrecedenceInGetProperty() throws IOException {
        String property = configuration.getStringProperty("precendence.test.property");
        assertThat(property, is("first"));
    }

}