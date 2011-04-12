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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderProcessor {
    private PlaceholderResolver placeholderResolver;

    PlaceholderProcessor() {
        placeholderResolver = new PlaceholderResolver();
    }

    PlaceholderProcessor(PlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    /**
     * Substitutes place holders in the supplied properties with the relevant values.
     *
     * Placeholders of the form ${propname} are substituted with appropriate system variable.
     * Placeholders of the form ${env.propname} are substituted with appropriate environment variable.
     *
     * @param propertiesWithSourcesList list of PropertiesWithSource containing properties which place holders to be
     *        substituted
     * @return list of PropertiesWithSource with substituted placeholders
     */
    public List<PropertiesWithSource> resolvePlaceholders(List<PropertiesWithSource> propertiesWithSourcesList) {
        List<PropertiesWithSource> resolvedPropertiesWithSourcesList = new ArrayList<PropertiesWithSource>();

        for (PropertiesWithSource propertiesWithSource : propertiesWithSourcesList) {
            Properties resolvedProperties = new Properties();
            for (Object key : propertiesWithSource.propertyKeys()) {
                resolvedProperties.put(
                        key,
                        substitutePlaceholders(propertiesWithSource.getStringProperty((String) key)));
            }
            resolvedPropertiesWithSourcesList.add(
                    new PropertiesWithSource(resolvedProperties, propertiesWithSource.getSource())
            );
        }

        return resolvedPropertiesWithSourcesList;
    }


    Pattern placeHolderRegexp = Pattern.compile("\\$\\{(.*?)\\}");

    private String substitutePlaceholders(String property) {
        StringBuffer resolvedProperty = new StringBuffer();

        Matcher matcher = placeHolderRegexp.matcher(property);
        while(matcher.find()) {
            matcher.appendReplacement(resolvedProperty, placeholderResolver.resolvePlaceholder(matcher.group(1)));
        }
        matcher.appendTail(resolvedProperty);

        return resolvedProperty.toString();
    }
}
