package com.niranjankagri.mobile.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** The header on every page: logo, cart link with its badge, and the menu that phones open with a button. */
public final class Header {

    private static final By CART_LINK = By.id("cart-link");
    private static final By CART_COUNT = By.id("cart-count");
    private static final By MENU_BUTTON = By.id("menu-button");
    private static final By MENU = By.id("main-nav");
    private static final By MENU_CHECKOUT = By.cssSelector("#main-nav a[href='checkout.html']");

    private final BasePage page;

    /** The header of one page; it acts through that page's waits and taps. */
    Header(BasePage page) {
        this.page = page;
    }

    /** The number on the cart badge. */
    public int cartCount() {
        return Integer.parseInt(page.visible(CART_COUNT).getText().trim());
    }

    /** Taps the cart link. */
    public CartPage openCart() {
        page.tap(CART_LINK);
        return new CartPage(page.driver).await();
    }

    /** Taps the Menu button (opens the menu when it is closed). */
    public Header tapMenuButton() {
        page.tap(MENU_BUTTON);
        return this;
    }

    /** Whether the menu is shown, for the eye. */
    public boolean isMenuShown() {
        return page.isDisplayed(MENU);
    }

    /** Whether the menu button tells screen readers the menu is open (aria-expanded). */
    public boolean menuButtonSaysOpen() {
        return "true".equals(page.visible(MENU_BUTTON).getAttribute("aria-expanded"));
    }

    /** Taps Checkout in the open menu. */
    public CheckoutPage checkoutFromMenu() {
        page.tap(MENU_CHECKOUT);
        return new CheckoutPage(page.driver).await();
    }

    /** The header's touch targets, for the size check. */
    public WebElement menuButton() {
        return page.visible(MENU_BUTTON);
    }

    public WebElement cartLink() {
        return page.visible(CART_LINK);
    }
}
