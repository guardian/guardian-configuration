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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StageResolverTest {

    @Mock StageResolver.FileLoader fileLoader;
    StageResolver resolver;

    @Before
    public void setUp() throws IOException {
        resolver = new StageResolver();
        resolver.setFileLoader(fileLoader);

        String properties = "int.service.domain=domain.gnl";
        InputStream inputStream = new ByteArrayInputStream(properties.getBytes("UTF-8"));
        when(fileLoader.getFile(anyString())).thenReturn(inputStream);
    }

    @Test
	public void shouldReadIntServiceDomain() throws IOException {
		assertThat(resolver.getIntServiceDomain(), is("domain.gnl"));
	}
}
