package com.gu.conf;

import org.junit.Test;

import static com.gu.conf.PropertiesSource.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class PropertiesSourceTest {

    @Test
	public void shouldCompareInPrecendenceOrder() {
		assertThat(INSTALLATION_PROPERTIES.compareTo(DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES), greaterThan(0));
		assertThat(DEV_OVERRIDE_SYSTEM_WEBAPP_PROPERTIES.compareTo(SYSTEM_WEBAPP_PROPERTIES), greaterThan(0));
		assertThat(SYSTEM_WEBAPP_PROPERTIES.compareTo(WEBAPP_GLOBAL_PROPERTIES), greaterThan(0));
		assertThat(WEBAPP_GLOBAL_PROPERTIES.compareTo(WEBAPP_STAGE_PROPERTIES), greaterThan(0));
	}
}