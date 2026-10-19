package com.niranjankagri.mobile.app.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** The tab bar at the bottom of the sample app: UI Elements and Web View. */
public final class TabBar {

    private static final By UI_ELEMENTS = AppiumBy.accessibilityId("UI Elements");
    private static final By WEB_VIEW = AppiumBy.accessibilityId("Web View");

    private final Screen screen;

    /** The tab bar of the screen it belongs to (screens create it). */
    TabBar(Screen screen) {
        this.screen = screen;
    }

    /** Taps the UI Elements tab and returns that screen once it is shown. */
    public UiElementsScreen openUiElements() {
        screen.visible(UI_ELEMENTS).click();
        return new UiElementsScreen(screen.driver).await();
    }

    /** Taps the Web View tab and returns that screen once it is shown. */
    public WebViewScreen openWebView() {
        screen.visible(WEB_VIEW).click();
        return new WebViewScreen(screen.driver).await();
    }

    /** A selected tab bar button reports the value "1". */
    public boolean isUiElementsSelected() {
        return "1".equals(screen.visible(UI_ELEMENTS).getAttribute("value"));
    }

    public boolean isWebViewSelected() {
        return "1".equals(screen.visible(WEB_VIEW).getAttribute("value"));
    }
}
