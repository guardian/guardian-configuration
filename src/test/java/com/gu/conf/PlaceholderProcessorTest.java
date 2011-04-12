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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaceholderProcessorTest {
    private PlaceholderProcessor placeholderProcessor;
    @Mock
    private PlaceholderResolver placeholderResolver;

    @Before
    public void setUp() {
        placeholderProcessor = new PlaceholderProcessor(placeholderResolver);
    }

    @Test
    public void shouldReplacePlaceholderWithResolvedValue() {
        Properties properties = new Properties();
        properties.put("aprop", "with a ${placeholder}");
        List<PropertiesWithSource> propertiesWithSource = Arrays.asList(new PropertiesWithSource(properties, "a source"));

        when(placeholderResolver.resolvePlaceholder("placeholder")).thenReturn("resolved placeholder");

        List<PropertiesWithSource> processedProperties = placeholderProcessor.resolvePlaceholders(propertiesWithSource);

        assertThat(processedProperties.get(0).getStringProperty("aprop"), is("with a resolved placeholder"));
    }
}
