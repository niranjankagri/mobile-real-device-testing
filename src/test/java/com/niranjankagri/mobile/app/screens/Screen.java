package com.niranjankagri.mobile.app.screens;

import io.appium.java_client.ios.IOSDriver;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * What every screen of the sample app shares: the driver, a wait, and the tab bar at the bottom. Locators come from
 * one discovery session's element tree (see docs/framework-design.md) and stay private to their screen.
 */
public abstract class Screen {

    protected final IOSDriver driver;
    protected final WebDriverWait wait;

    /** Every screen shares the driver and one explicit wait. */
    protected Screen(IOSDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /** The tab bar at the bottom of the app. */
    public TabBar tabBar() {
        return new TabBar(this);
    }

    /** Waits until the element is on screen; the real device needs a moment after each tap. */
    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
