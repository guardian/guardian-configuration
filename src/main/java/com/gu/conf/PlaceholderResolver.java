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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderResolver {
    private SystemWrapper systemWrapper;
    public PlaceholderResolver() {
        systemWrapper = new SystemWrapper();
    }

    PlaceholderResolver(SystemWrapper systemWrapper) {
        this.systemWrapper = systemWrapper;
    }

    private static final Pattern placeholderRegex = Pattern.compile("(env\\.)?(.*)");
    public String resolvePlaceholder(String placeholder) {
        Matcher matcher = placeholderRegex.matcher(placeholder);
        matcher.matches();

        if(matcher.group(1) == null) {
            return systemWrapper.getProperty(matcher.group(2));
        } else {
            return systemWrapper.getenv(matcher.group(2));
        }
    }
}
