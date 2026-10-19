package com.niranjankagri.mobile.app.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.By;

/** The second tab: a web page shown inside the native app. */
public final class WebViewScreen extends Screen {

    private static final By WEB_VIEW = AppiumBy.className("XCUIElementTypeWebView");
    private static final By LINKS_IN_PAGE = AppiumBy.iOSClassChain("**/XCUIElementTypeWebView/**/XCUIElementTypeLink");

    /** Created by the tab bar, never by tests. */
    WebViewScreen(IOSDriver driver) {
        super(driver);
    }

    /** Waits until the web view element is on screen; awaitPageLoaded() waits for its content. */
    WebViewScreen await() {
        visible(WEB_VIEW);
        return this;
    }

    /**
     * Waits until the page inside has loaded. The page is browserstack.com and changes over time, so this checks the
     * structure, not the wording: a loaded page exposes its links to the accessibility tree inside the web view.
     */
    public WebViewScreen awaitPageLoaded() {
        wait.until(d -> !d.findElements(LINKS_IN_PAGE).isEmpty());
        return this;
    }
}
