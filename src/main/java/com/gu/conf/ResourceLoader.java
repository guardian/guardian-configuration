package com.gu.conf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {

    public InputStream getResource(String resource) throws IOException {
        ClassLoader classloader = ResourceLoader.class.getClassLoader();
        URL url = classloader.getResource(resource);

        return url.openStream();
    }
}