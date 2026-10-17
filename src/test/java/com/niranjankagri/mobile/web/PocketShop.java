package com.niranjankagri.mobile.web;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The shop's pages and actions, with waits built in, so the tests read as user steps. Element ids come from the site
 * (src/main/resources/site).
 */
public final class PocketShop {

    private final WebDriver driver;
    private final String baseUrl;
    private final WebDriverWait wait;

    PocketShop(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
        // Generous: a real device over the tunnel is slower than local Chrome. Waits end as soon as the condition holds.
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void open(String page) {
        driver.get(baseUrl + page);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
    }

    public WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void tap(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Taps a button right after typing, the way a person does: close the on-screen keyboard first, wait until the
     * layout has stopped moving, then tap. On the real Android phone a tap sent while the keyboard was open landed on
     * {@code <html>} instead of the visible button (found with a probe on the first device runs), so the form was never
     * submitted. Local Chrome and iPhone Safari were not affected.
     */
    public void tapAfterTyping(By locator) {
        script("document.activeElement && document.activeElement.blur();");
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

    public void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }

    /**
     * Waits until the page heading is the expected one. After a tap that navigates, reading the heading at once could
     * see the old page if the browser returns from the click before the next page has loaded; waiting for the expected
     * text cannot. (This was my first guess for the Android checkout failure; the probe showed the real cause, see
     * {@link #tapAfterTyping}, but the stricter check stays.)
     */
    public void awaitHeading(String expected) {
        wait.until(ExpectedConditions.textToBe(By.tagName("h1"), expected));
    }

    public int cartCount() {
        return Integer.parseInt(visible(By.id("cart-count")).getText().trim());
    }

    public List<WebElement> productCards() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("[data-product]"), 0));
        return driver.findElements(By.cssSelector("[data-product]"));
    }

    /** Opens a product from the list, sets the quantity with the + button and adds it to the cart. */
    public void addToCart(String productName, int quantity) {
        open("index.html");
        tap(By.linkText(productName));
        visible(By.id("product-name"));
        for (int i = 1; i < quantity; i++) {
            tap(By.id("more"));
        }
        tap(By.id("add-to-cart"));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("added"), "Added " + quantity));
    }

    public Object script(String javascript, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(javascript, args);
    }

    /** Size and position of an element in CSS pixels, as the page lays it out. */
    @SuppressWarnings("unchecked")
    public Map<String, Number> box(WebElement element) {
        return (Map<String, Number>) script(
                "const r = arguments[0].getBoundingClientRect(); return {x: r.x, y: r.y, width: r.width, height: r.height};",
                element);
    }

    public WebDriverWait waiting() {
        return wait;
    }
}
