package com.niranjankagri.mobile.config;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * BrowserStack account and session settings. The credentials come from the environment variables
 * BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY (in CI: repository secrets); they are never in the repository
 * and never printed.
 */
public final class BrowserStack {

    public static final URL HUB = url("https://hub-cloud.browserstack.com/wd/hub");

    /** One tunnel per run: the identifier ties this run's sessions to this run's tunnel. */
    public static final String LOCAL_IDENTIFIER = "run-" + UUID.randomUUID();

    /** Groups the sessions of one run in the BrowserStack dashboard. */
    public static final String BUILD = System.getProperty("bsBuild", "local-" + System.currentTimeMillis());

    private BrowserStack() {}

    public static String username() {
        return required("BROWSERSTACK_USERNAME");
    }

    public static String accessKey() {
        return required("BROWSERSTACK_ACCESS_KEY");
    }

    /** The "bstack:options" every session gets: account, project, build and session name. */
    public static Map<String, Object> sessionOptions(String sessionName) {
        Map<String, Object> options = new HashMap<>();
        options.put("userName", username());
        options.put("accessKey", accessKey());
        options.put("projectName", "mobile-real-device-testing");
        options.put("buildName", BUILD);
        options.put("sessionName", sessionName);
        // End an idle session after 2 minutes instead of the default 5: a hung run must not eat the minutes.
        options.put("idleTimeout", 120);
        return options;
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " is not set. BrowserStack runs need BROWSERSTACK_USERNAME and "
                    + "BROWSERSTACK_ACCESS_KEY (see README); local runs (-Dtarget=local) need neither.");
        }
        return value;
    }

    private static URL url(String value) {
        try {
            return URI.create(value).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException(value, e);
        }
    }
}
