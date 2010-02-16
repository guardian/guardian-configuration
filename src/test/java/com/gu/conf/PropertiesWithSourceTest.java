package com.gu.conf;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PropertiesWithSourceTest {

    @Test
    public void shouldBeIdeologicallyPure() {
        PropertiesWithSource properties = new PropertiesBuilder()
            .property("property", "theft")
            .toPropertiesWithSource();

        assertThat(properties.getStringProperty("property"), is("theft"));
    }
}