package com.niranjankagri.mobile.web;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.niranjankagri.mobile.web.pages.BasePage;
import com.niranjankagri.mobile.web.pages.Header;
import com.niranjankagri.mobile.web.pages.ProductListPage;
import java.util.List;
import java.util.function.Supplier;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/** What makes a page work on a phone screen: viewport, one column, no sideways scrolling, a menu that fits. */
public class MobileLayoutTest extends WebTestBase {

    @Test(description = "The browser really is phone-sized and touch-capable (proves the target, real or emulated)")
    public void browserIsAPhone() {
        ProductListPage page = shop.openProductList();
        assertTrue(page.viewportWidth() <= 430, "viewport width " + page.viewportWidth() + " CSS px is not a phone");
        assertTrue(page.maxTouchPoints() > 0, "no touch support");
    }

    @Test(description = "The viewport meta tag is present, otherwise phones render the page 980 px wide and shrink it")
    public void viewportIsSetForPhones() {
        assertEquals(shop.openProductList().viewportMeta(), "width=device-width, initial-scale=1");
    }

    @Test(description = "Products are in one column on a phone")
    public void productsAreInOneColumn() {
        ProductListPage page = shop.openProductList();
        List<WebElement> cards = page.productCards();
        assertEquals(cards.size(), 3);
        // One column: every card starts at the same x. In a grid the second card would sit to the right of the first.
        double firstX = page.box(cards.get(0)).get("x").doubleValue();
        for (WebElement card : cards) {
            assertEquals(page.box(card).get("x").doubleValue(), firstX, 0.5, "cards are not stacked in one column");
        }
    }

    @Test(description = "No page scrolls sideways on a phone")
    public void noPageScrollsSideways() {
        List<Supplier<BasePage>> pages = List.of(
                shop::openProductList, () -> shop.openProduct("lamp"), shop::openCart, shop::openCheckout);
        for (Supplier<BasePage> open : pages) {
            BasePage page = open.get();
            int overflow = page.sidewaysOverflow();
            assertEquals(overflow, 0, page.getClass().getSimpleName() + " scrolls sideways by " + overflow + " px");
        }
    }

    @Test(description = "The menu is behind a button on phones, opens on tap and leads to its pages")
    public void menuOpensFromTheMenuButton() {
        Header header = shop.openProductList().header();
        // Closed at first, for the eye and for screen readers
        assertFalse(header.isMenuShown(), "the menu should be closed at first");
        assertFalse(header.menuButtonSaysOpen());

        // Open it with a tap
        header.tapMenuButton();
        assertTrue(header.isMenuShown(), "the menu did not open");
        assertTrue(header.menuButtonSaysOpen());

        // A link in the open menu leads to its page (checkoutFromMenu waits for the Checkout heading)
        header.checkoutFromMenu();
    }
}
