package com.niranjankagri.mobile.driver;

import com.niranjankagri.mobile.config.BrowserStack;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import java.util.Map;

/**
 * Native app sessions on a real iPhone at BrowserStack App Automate, driven by Appium's XCUITest driver.
 *
 * The app is BrowserStack's sample iOS app, uploaded once with the custom id "bstack-sample-ios" (see README). A real
 * iPhone only runs apps signed for it: BrowserStack re-signs uploads with its own certificates, which needs an app
 * built with a development or ad-hoc profile. Many public demo apps are signed for another cloud and are rejected.
 */
public final class AppDrivers {

    /** The uploaded app: a custom id or a bs:// app URL; -DbsApp=... tests another upload. */
    public static final String APP = System.getProperty("bsApp", "bstack-sample-ios");

    /** Factory methods only: no instances. */
    private AppDrivers() {}

    /** Starts the app on a real iPhone 16 with iOS 18. Device and OS must be a pair BrowserStack offers. */
    public static IOSDriver iphone(String sessionName) {
        XCUITestOptions options = new XCUITestOptions().setApp(APP);
        Map<String, Object> bstack = BrowserStack.sessionOptions(sessionName);
        bstack.put("deviceName", "iPhone 16");
        bstack.put("osVersion", "18");
        options.setCapability("bstack:options", bstack);
        return new IOSDriver(BrowserStack.HUB, options);
    }
}
