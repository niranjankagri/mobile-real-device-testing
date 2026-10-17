package com.niranjankagri.mobile.web;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/** What makes a page work on a phone screen: viewport, one column, no sideways scrolling, a menu that fits. */
public class MobileLayoutTest extends WebTestBase {

    @Test(description = "The browser really is phone-sized and touch-capable (proves the target, real or emulated)")
    public void browserIsAPhone() {
        Number width = (Number) shop.script("return window.innerWidth;");
        Number touchPoints = (Number) shop.script("return navigator.maxTouchPoints;");
        assertTrue(width.intValue() <= 430, "viewport width " + width + " CSS px is not a phone");
        assertTrue(touchPoints.intValue() > 0, "no touch support");
    }

    @Test(description = "The viewport meta tag is present, otherwise phones render the page 980 px wide and shrink it")
    public void viewportIsSetForPhones() {
        String content = (String) shop.script("return document.querySelector('meta[name=viewport]')?.content;");
        assertEquals(content, "width=device-width, initial-scale=1");
    }

    @Test(description = "Products are in one column on a phone")
    public void productsAreInOneColumn() {
        List<WebElement> cards = shop.productCards();
        assertEquals(cards.size(), 3);
        double firstX = shop.box(cards.get(0)).get("x").doubleValue();
        for (WebElement card : cards) {
            Map<String, Number> box = shop.box(card);
            assertEquals(box.get("x").doubleValue(), firstX, 0.5, "cards are not stacked in one column");
        }
    }

    @Test(description = "No page scrolls sideways on a phone")
    public void noPageScrollsSideways() {
        for (String page : List.of("index.html", "product.html?id=lamp", "cart.html", "checkout.html")) {
            shop.open(page);
            Number overflow = (Number) shop.script(
                    "return document.documentElement.scrollWidth - document.documentElement.clientWidth;");
            assertEquals(overflow.intValue(), 0, page + " scrolls sideways by " + overflow + " px");
        }
    }

    @Test(description = "The menu is behind a button on phones, opens on tap and leads to its pages")
    public void menuOpensFromTheMenuButton() {
        By menu = By.id("menu-button");
        By nav = By.id("main-nav");
        assertFalse(shop.visible(menu).getAttribute("aria-expanded").equals("true"));
        assertFalse(isDisplayed(nav), "the menu should be closed at first");

        shop.tap(menu);
        assertTrue(isDisplayed(nav), "the menu did not open");
        assertEquals(shop.visible(menu).getAttribute("aria-expanded"), "true");

        shop.tap(By.cssSelector("#main-nav a[href='checkout.html']"));
        shop.awaitHeading("Checkout");
    }

    private boolean isDisplayed(By locator) {
        return shop.waiting().until(d -> d.findElement(locator)).isDisplayed();
    }
}
