package com.niranjankagri.mobile.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** product.html?id=...: one product, the quantity stepper and the Add to cart button. */
public final class ProductPage extends BasePage {

    private static final By NAME = By.id("product-name");
    private static final By LESS = By.id("less");
    private static final By MORE = By.id("more");
    private static final By QUANTITY = By.id("quantity");
    private static final By ADD_TO_CART = By.id("add-to-cart");
    private static final By ADDED = By.id("added");

    ProductPage(WebDriver driver) {
        super(driver);
    }

    ProductPage await() {
        // app.js fills in the name; the placeholder heading is "Product"
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(NAME, "Product")));
        return this;
    }

    /** Sets the quantity with the + button: it starts at 1, each tap adds one. */
    public ProductPage setQuantity(int quantity) {
        for (int i = 1; i < quantity; i++) {
            tap(MORE);
        }
        return this;
    }

    /** The quantity shown between the - and + buttons. */
    public int quantity() {
        return Integer.parseInt(visible(QUANTITY).getText().trim());
    }

    /** Taps Add to cart and waits for the status message, so the cart is updated before the test goes on. */
    public ProductPage addToCart() {
        int quantity = quantity();
        tap(ADD_TO_CART);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(ADDED, "Added " + quantity));
        return this;
    }

    // Controls, for the touch target and thumb reach checks

    public WebElement lessButton() {
        return visible(LESS);
    }

    public WebElement moreButton() {
        return visible(MORE);
    }

    public WebElement addToCartButton() {
        return visible(ADD_TO_CART);
    }
}
