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
        PropertiesBuilder systemWebapp = new PropertiesBuilder();
        systemWebapp.property("precendence.test.property", "first");
        systemWebapp.property("double.property", "25.0");
        systemWebapp.systemWebappProperties();

        PropertiesBuilder webappStage = new PropertiesBuilder();
        webappStage.property("precendence.test.property", "second");
        webappStage.property("integer.property", "23");
        webappStage.property("nonnumeric.property", "qwe");
        webappStage.webappStageProperties();

        ImmutableList<PropertiesWithSource> properties = ImmutableList.of(
                systemWebapp.toPropertiesWithSource(),
                webappStage.toPropertiesWithSource()
        );

        configuration = new Configuration(properties);
    }

    @Test
    public void shouldGetPropertySource() throws IOException {
        PropertiesSource propertySource = configuration.getPropertySource("nonnumeric.property");
        assertThat(propertySource, is(PropertiesSource.WEBAPP_STAGE_PROPERTIES));
    }

    @Test
    public void shouldGetNullForPropertySourceIfNotSet() throws IOException {
        PropertiesSource propertySource = configuration.getPropertySource("nosuch.property");
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
        PropertiesSource propertySource = configuration.getPropertySource("precendence.test.property");
        assertThat(propertySource, is(PropertiesSource.SYSTEM_WEBAPP_PROPERTIES));
    }

    @Test
    public void shouldRespectFirstDeclarationPrecedenceInGetProperty() throws IOException {
        String property = configuration.getStringProperty("precendence.test.property");
        assertThat(property, is("first"));
    }

}