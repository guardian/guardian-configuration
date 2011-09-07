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

package com.gu.conf.impl;

import com.gu.conf.impl.PrinterUtil;
import org.junit.Test;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PrinterUtilTest {
    @Test
    public void shouldReplacePasswordWithPlaceholder() {
        assertThat(PrinterUtil.propertyString("password", "abc123"), endsWith("*** PASSWORD ****\n"));
        assertThat(PrinterUtil.propertyString("foo.password.blah", "abc123"), endsWith("*** PASSWORD ****\n"));
        assertThat(PrinterUtil.propertyString("blah.pass.foo", "abc123"), endsWith("*** PASSWORD ****\n"));
    }

    @Test
    public void shouldReplaceKeyWithPlaceholder() {
        assertThat(PrinterUtil.propertyString("key", "abc123"), endsWith("*** KEY ****\n"));
        assertThat(PrinterUtil.propertyString("foo.key.blah", "abc123"), endsWith("*** KEY ****\n"));
    }
    @Test
    public void shouldFormatKeyValue() {
        assertThat(PrinterUtil.propertyString("akey", "value"), is("akey=value\n"));
    }
}
