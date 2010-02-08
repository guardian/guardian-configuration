package com.gu.conf;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ConfigurationTest {

    Configuration configuration;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.setProperty("integer.property", "23");
        properties.setProperty("double.property", "25.0");
        properties.setProperty("nonnumeric.property", "qwe");

        configuration = new Configuration(properties);
    }

    @Test
    public void shouldGetProperty() throws IOException {
        String property = configuration.getProperty("nonnumeric.property");
        assertThat(property, is("qwe"));
    }

    @Test
    public void shouldGetNullForPropertyIfNotSet() throws IOException {
        String property = configuration.getProperty("nosuch.property");
        assertThat(property, nullValue());
    }

    @Test
    public void shouldGetDefaultForPropertyIfNotSet() throws IOException {
        String property = configuration.getProperty("nosuch.property", "default");
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
}