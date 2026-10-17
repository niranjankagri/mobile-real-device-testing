package com.niranjankagri.mobile.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** cart.html: one line per product, the total and the checkout link. */
public final class CartPage extends BasePage {

    public static final String PATH = "cart.html";

    private static final By TOTAL = By.id("cart-total");
    private static final By CHECKOUT = By.id("checkout-link");

    CartPage(WebDriver driver) {
        super(driver);
    }

    CartPage await() {
        awaitHeading("Your cart");
        return this;
    }

    /** The text of a product's line, for example "2 × Folding desk lamp\n€ 68.00". */
    public String line(String productId) {
        return visible(By.cssSelector("[data-line='" + productId + "']")).getText();
    }

    /** The total, for example "€ 68.00". */
    public String total() {
        return visible(TOTAL).getText();
    }

    /** Taps Checkout. */
    public CheckoutPage checkout() {
        tap(CHECKOUT);
        return new CheckoutPage(driver).await();
    }
}
