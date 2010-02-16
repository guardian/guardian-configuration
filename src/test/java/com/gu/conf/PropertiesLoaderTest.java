package com.gu.conf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static com.gu.conf.PropertiesSource.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesLoaderTest {

    @Mock FileAndResourceLoader fileLoader;
    PropertiesLoader loader;

    @Before
    public void setUp() throws IOException {
        loader = new PropertiesLoader(fileLoader);

        // INSTALLATION_PROPERTIES
        Properties properties = new PropertiesBuilder()
            .property("source", "installation.properties")
            .property("int.service.domain", "domain.gnl")
            .property("stage", "DEV")
            .toProperties();
        when(fileLoader.getPropertiesFromFile("/etc/gu/installation.properties")).thenReturn(properties);

        // DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "dev.override.system.webapp.properties")
            .toProperties();
        when(fileLoader.getPropertiesFromFile("~/etc/gu/webapp.properties")).thenReturn(properties);

        // SYSTEM_WEBAPP_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "system.webapp.properties")
            .toProperties();
        when(fileLoader.getPropertiesFromFile("/etc/gu/webapp.properties")).thenReturn(properties);

        // WEBAPP_GLOBAL_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "webapp.global.properties")
            .toProperties();
        when(fileLoader.getPropertiesFromResource("/conf/global.properties")).thenReturn(properties);

        // WEBAPP_STAGE_PROPERTIES
        properties = new PropertiesBuilder()
            .property("source", "webapp.stage.properties")
            .toProperties();
        when(fileLoader.getPropertiesFromResource("/conf/domain.gnl.properties")).thenReturn(properties);
    }

    @Test
	public void shouldReadIntServiceDomain() throws IOException {
		assertThat(loader.getIntServiceDomain(), is("domain.gnl"));
	}

    @Test
	public void shouldReadStage() throws IOException {
		assertThat(loader.getStage(), is("DEV"));
	}

    @Test
    public void shouldLoadInstallationProperties() {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, INSTALLATION_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(loader.getStage(), is("DEV"));
        assertThat(properties.getStringProperty("source"), is("installation.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadDevOverrideSystemWebappPropertiesIfDevStage() {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(loader.getStage(), is("DEV"));
        assertThat(properties.getStringProperty("source"), is("dev.override.system.webapp.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldNotLoadDevOverrideSystemWebappPropertiesIfNotDevStage() {
        // INSTALLATION_PROPERTIES
        Properties installation = new PropertiesBuilder()
            .property("stage", "PROD")
            .toProperties();
        when(fileLoader.getPropertiesFromFile("/etc/gu/installation.properties")).thenReturn(installation);

        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES);

        assertThat(loader.getStage(), not("DEV"));
        assertThat(properties, nullValue());
    }

    @Test
    public void shouldLoadSystemWebappProperties() {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, SYSTEM_WEBAPP_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("system.webapp.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadWebappGlobalProperties() {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, WEBAPP_GLOBAL_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("webapp.global.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadWebappStageProperties() {
        List<PropertiesWithSource> propertiesList = loader.getProperties("webapp", "/conf");
        PropertiesWithSource properties = getPropertiesWithSource(propertiesList, WEBAPP_STAGE_PROPERTIES);

        assertThat(properties, notNullValue());
        assertThat(properties.getStringProperty("source"), is("webapp.stage.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }
    
    private PropertiesWithSource getPropertiesWithSource(List<PropertiesWithSource> propertiesList,
            PropertiesSource source) {
        for (PropertiesWithSource properties : propertiesList) {
            if (properties.getSource().equals(source)) {
                return properties;
            }
        }

        return null;
    }
}
