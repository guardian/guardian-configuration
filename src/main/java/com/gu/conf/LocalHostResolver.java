package com.gu.conf;

import org.xbill.DNS.Address;

import java.io.IOException;
import java.net.InetAddress;

public class LocalHostResolver {

    public InetAddress getLocalHost() throws IOException {
        // We need an explicit DNS lookup because InetAddress.getLocalHost
        // doesn't return domain information in our configuration.
        String localHostName = InetAddress.getLocalHost().getHostName();

        return Address.getByName(localHostName);
    }

    public String getLocalHostDomain() throws IOException {
        String localHost = getLocalHost().getCanonicalHostName();

        return localHost.substring(localHost.indexOf('.') + 1);
    }
}