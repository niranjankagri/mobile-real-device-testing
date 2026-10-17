package com.niranjankagri.mobile.config;

import java.util.Locale;

/**
 * Where the web suite runs. LOCAL costs nothing and runs everywhere; the real devices use BrowserStack minutes.
 */
public enum Target {

    /** Chrome on this machine, emulating an iPhone-sized screen (390 x 844 CSS px, touch, mobile user agent). */
    LOCAL("Chrome mobile emulation", "localhost"),
    /** Safari on a real iPhone at BrowserStack. */
    IPHONE("Safari on a real iPhone (BrowserStack)", "bs-local.com"),
    /** Chrome on a real Android phone at BrowserStack. */
    ANDROID("Chrome on a real Android phone (BrowserStack)", "bs-local.com");

    private final String description;
    /**
     * Host name of PocketShop as the browser sees it. On BrowserStack, "bs-local.com" is routed through the tunnel to
     * this machine; iOS Safari does not send "localhost" through it.
     */
    private final String host;

    Target(String description, String host) {
        this.description = description;
        this.host = host;
    }

    /** The target from -Dtarget=local|iphone|android (default local). */
    public static Target current() {
        String value = System.getProperty("target", "local").trim();
        try {
            return valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("-Dtarget must be local, iphone or android, not \"" + value + "\"", e);
        }
    }

    /** True for the real devices: they need credentials and the tunnel. */
    public boolean onBrowserStack() {
        return this != LOCAL;
    }

    /** Readable name, used in the BrowserStack session name. */
    public String description() {
        return description;
    }

    /** Address of PocketShop for this target, for example http://bs-local.com:4330/. */
    public String baseUrl(int port) {
        return "http://" + host + ":" + port + "/";
    }
}
