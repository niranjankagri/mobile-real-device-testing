package com.niranjankagri.mobile.web.pages;

import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * What every PocketShop page shares: the header, waits, taps, typing, and measurements of the page as the phone lays
 * it out. Page classes keep their locators private; tests only call page methods.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private final Header header;

    /** Page objects are created by PocketShop or by the page an action leads from. */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        // Generous: a real device over the tunnel is slower than local Chrome. Waits end as soon as the condition holds.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.header = new Header(this);
    }

    /** The header on every page: logo, cart badge, menu. */
    public Header header() {
        return header;
    }

    /** Waits until the page heading is the expected one, so a test never reads the previous page after a tap. */
    protected void awaitHeading(String expected) {
        wait.until(ExpectedConditions.textToBe(By.tagName("h1"), expected));
    }

    // ---------------------------------------------------------------- actions for page classes

    /** Waits until the element is visible and returns it. */
    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Whether the element is shown; for elements that exist on the page but can be hidden. */
    protected boolean isDisplayed(By locator) {
        return wait.until(d -> d.findElement(locator)).isDisplayed();
    }

    /** Waits until the element can be clicked and taps it (on a phone, Selenium's click is a tap). */
    protected void tap(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** Replaces the text of a field. On a real phone this types on the on-screen keyboard. */
    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }

    /**
     * Taps a button right after typing, the way a person does: close the on-screen keyboard first, wait until the
     * layout has stopped moving, then tap. On the real Android phone a tap sent while the keyboard was open landed on
     * {@code <html>} instead of the visible button (found with a probe on the first device runs), so the form was never
     * submitted. Local Chrome and iPhone Safari were not affected.
     */
    protected void tapAfterTyping(By locator) {
        // Blur the focused field: the on-screen keyboard closes and the page gets its full height back.
        script("document.activeElement && document.activeElement.blur();");
        // Then wait until the button stays at the same height on screen: the layout has stopped moving.
        wait.until(d -> {
            Number before = (Number) script("return arguments[0].getBoundingClientRect().y;", d.findElement(locator));
            try {
                Thread.sleep(300); // two measurements 300 ms apart: the keyboard's closing animation must be over
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Number after = (Number) script("return arguments[0].getBoundingClientRect().y;", d.findElement(locator));
            return before.doubleValue() == after.doubleValue();
        });
        tap(locator);
    }

    /** Runs JavaScript in the page: for what a user sees but WebDriver does not report (sizes, focus, storage). */
    protected Object script(String javascript, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(javascript, args);
    }

    // ---------------------------------------------------------------- measurements for the tests

    /** Size and position of an element in CSS pixels, as the page lays it out. */
    @SuppressWarnings("unchecked")
    public Map<String, Number> box(WebElement element) {
        return (Map<String, Number>) script(
                "const r = arguments[0].getBoundingClientRect(); return {x: r.x, y: r.y, width: r.width, height: r.height};",
                element);
    }

    /** Width of the screen in CSS px: phones are about 360 to 430 (390 in the emulation); a desktop window is 1000+. */
    public int viewportWidth() {
        return ((Number) script("return window.innerWidth;")).intValue();
    }

    /** Height of the visible page in CSS px (smaller while the on-screen keyboard is open). */
    public int viewportHeight() {
        return ((Number) script("return window.innerHeight;")).intValue();
    }

    /** How many fingers the screen supports at once; 0 means no touch screen. */
    public int maxTouchPoints() {
        return ((Number) script("return navigator.maxTouchPoints;")).intValue();
    }

    /** The content of the viewport meta tag, or null when it is missing. */
    public String viewportMeta() {
        return (String) script("return document.querySelector('meta[name=viewport]')?.content;");
    }

    /**
     * How far the page scrolls sideways, in CSS px. Content wider than the screen makes scrollWidth larger than
     * clientWidth; 0 means it fits.
     */
    public int sidewaysOverflow() {
        return ((Number) script("return document.documentElement.scrollWidth - document.documentElement.clientWidth;"))
                .intValue();
    }
}
