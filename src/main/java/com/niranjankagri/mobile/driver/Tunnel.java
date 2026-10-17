package com.niranjankagri.mobile.driver;

import com.browserstack.local.Local;
import com.niranjankagri.mobile.config.BrowserStack;
import java.util.HashMap;
import java.util.Map;

/**
 * The BrowserStack Local tunnel: real devices in BrowserStack's data centre open "bs-local.com" and reach the site
 * running on this machine, the same way teams test a staging server that is not on the internet. The library
 * downloads BrowserStack's tunnel binary on first use.
 */
public final class Tunnel implements AutoCloseable {

    private final Local local = new Local();

    /** Starts the tunnel and waits until it is connected; the device sessions are created after this. */
    public static Tunnel open() {
        Tunnel tunnel = new Tunnel();
        Map<String, String> args = new HashMap<>();
        args.put("key", BrowserStack.accessKey());
        args.put("localIdentifier", BrowserStack.LOCAL_IDENTIFIER);
        // Route all traffic of the sessions through this machine, so the device sees the same site as the tests.
        args.put("forcelocal", "true");
        try {
            tunnel.local.start(args);
        } catch (Exception e) {
            throw new IllegalStateException("BrowserStack Local tunnel did not start: " + e.getMessage(), e);
        }
        return tunnel;
    }

    @Override
    public void close() {
        try {
            local.stop();
        } catch (Exception e) {
            // The tunnel process ends with the JVM anyway.
        }
    }
}
