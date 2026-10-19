package com.niranjankagri.mobile.app.screens;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The first tab: a text field with its output, and a button that opens a native alert. */
public final class UiElementsScreen extends Screen {

    // Accessibility ids: stable, set by the app's developers, and the same names VoiceOver reads out.
    private static final By TEXT_BUTTON = AppiumBy.accessibilityId("Text Button");
    private static final By TEXT_INPUT = AppiumBy.accessibilityId("Text Input");
    private static final By TEXT_OUTPUT = AppiumBy.accessibilityId("Text Output");
    private static final By ALERT_BUTTON = AppiumBy.accessibilityId("Alert");
    // No accessibility id for the title: the iOS element type, found fast by a class chain.
    private static final By NAV_TITLE = AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar");

    /** Created by the tab bar or atStart(), never by tests. */
    UiElementsScreen(IOSDriver driver) {
        super(driver);
    }

    /** The screen the app starts on. */
    public static UiElementsScreen atStart(IOSDriver driver) {
        return new UiElementsScreen(driver).await();
    }

    /** Waits until the screen is shown (its Text button is visible). */
    UiElementsScreen await() {
        visible(TEXT_BUTTON);
        return this;
    }

    /** The title in the navigation bar. */
    public String title() {
        return visible(NAV_TITLE).getAttribute("name");
    }

    /** Opens the text field, types on the iPhone keyboard and presses Return, which tells the app the input is done. */
    public UiElementsScreen enterText(String text) {
        visible(TEXT_BUTTON).click();
        visible(TEXT_INPUT).sendKeys(text + "\n");
        return this;
    }

    /** Waits until the app shows the text back, and returns it. */
    public String shownText(String expected) {
        wait.until(ExpectedConditions.attributeToBe(TEXT_OUTPUT, "value", expected));
        return visible(TEXT_OUTPUT).getAttribute("value");
    }

    /** Taps the Alert button and returns the native iOS alert it opens. */
    public Alert openAlert() {
        visible(ALERT_BUTTON).click();
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /** Whether the screen can be used: its buttons are shown (for example after an alert was closed). */
    public boolean isUsable() {
        return visible(ALERT_BUTTON).isDisplayed() && visible(TEXT_BUTTON).isDisplayed();
    }
}
