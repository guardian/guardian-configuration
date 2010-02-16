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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesLoaderTest {

    @Mock FileAndResourceLoader fileLoader;
    PropertiesLoader loader;

    @Before
    public void setUp() throws IOException {
        loader = new PropertiesLoader(fileLoader);

        // INSTALLATION_PROPERTIES
        PropertiesBuilder propertiesBuilder = new PropertiesBuilder();
        propertiesBuilder.property("source", "installation.properties");
        propertiesBuilder.property("int.service.domain", "domain.gnl");
        propertiesBuilder.property("stage", "DEV");
        Properties properties = propertiesBuilder.toProperties();
        when(fileLoader.getPropertiesFromFile("/etc/gu/installation.properties")).thenReturn(properties);

        // DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES

        // SYSTEM_WEBAPP_PROPERTIES
        propertiesBuilder = new PropertiesBuilder();
        propertiesBuilder.property("source", "system.webapp.properties");
        properties = propertiesBuilder.toProperties();
        when(fileLoader.getPropertiesFromFile("/etc/gu/webapp.properties")).thenReturn(properties);

        // WEBAPP_GLOBAL_PROPERTIES
        propertiesBuilder = new PropertiesBuilder();
        propertiesBuilder.property("source", "webapp.global.properties");
        properties = propertiesBuilder.toProperties();
        when(fileLoader.getPropertiesFromResource("/conf/global.properties")).thenReturn(properties);

        // WEBAPP_STAGE_PROPERTIES
        propertiesBuilder = new PropertiesBuilder();
        propertiesBuilder.property("source", "webapp.stage.properties");
        properties = propertiesBuilder.toProperties();
        when(fileLoader.getPropertiesFromResource("/conf/domain.gnl.properties")).thenReturn(properties);

//        propertiesBuilder.property("property", "theft");
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
        assertThat(properties.getStringProperty("source"), is("installation.properties"));
        assertThat(properties.getStringProperty("no-property"), nullValue());
    }

    @Test
    public void shouldLoadDevOverrideSystemWebappPropertiesIfDevStage() {
    }

    @Test
    public void shouldNotLoadDevOverrideSystemWebappPropertiesIfNotDevStage() {
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
