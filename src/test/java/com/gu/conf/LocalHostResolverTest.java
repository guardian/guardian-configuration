package com.gu.conf;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

public class LocalHostResolverTest {

	LocalHostResolver resolver = new LocalHostResolver();

    @Test
	public void shouldNotGetEmptyDomain() throws IOException {
		assertThat(resolver.getLocalHostDomain(), notNullValue());
	}

    @Test
	public void shouldNotGetLocalHostForDomain() throws IOException {
		assertThat(resolver.getLocalHostDomain(), not("localhost"));
	}

    @Test
	public void shouldGetDomainWithAtLeastOneDot() throws IOException {
		assertThat(resolver.getLocalHostDomain().contains("."), is(true));
	}
}
