package com.niranjankagri.mobile.web;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

/** The shop's main journey, done with taps and the on-screen keyboard. */
public class ShoppingFlowTest extends WebTestBase {

    @Test(description = "Adding a product with quantity 2 updates the cart badge and the cart")
    public void addingToCartUpdatesTheBadge() {
        shop.addToCart("Folding desk lamp", 2);
        assertEquals(shop.cartCount(), 2);

        shop.tap(By.id("cart-link"));
        assertEquals(shop.visible(By.cssSelector("[data-line='lamp']")).getText(), "2 × Folding desk lamp\n€ 68.00");
        assertEquals(shop.visible(By.id("cart-total")).getText(), "€ 68.00");
    }

    @Test(description = "The cart survives a reload: it is kept on the device")
    public void cartSurvivesAReload() {
        shop.addToCart("Steel water bottle", 1);
        shop.open("cart.html");
        assertEquals(shop.cartCount(), 1);
        assertEquals(shop.visible(By.id("cart-total")).getText(), "€ 19.90");
    }

    @Test(description = "Checkout with invalid details shows the errors and moves the cursor to the first one")
    public void invalidCheckoutShowsErrors() {
        shop.open("checkout.html");
        shop.type(By.id("name"), "Mia Wagner");
        shop.type(By.id("email"), "mia-at-example");
        shop.tapAfterTyping(By.id("place-order"));

        assertEquals(shop.visible(By.id("email-error")).getText(), "Enter an email like name@example.com");
        assertEquals(shop.visible(By.id("phone-error")).getText(), "Enter a phone number");
        assertEquals(shop.visible(By.id("email")).getAttribute("aria-invalid"), "true");
        Object focused = shop.script("return document.activeElement.id;");
        assertEquals(focused, "email", "focus should move to the first invalid field");
    }

    @Test(description = "A complete purchase on the phone: add, cart, checkout, confirmation, empty cart")
    public void completePurchase() {
        shop.addToCart("Enamel camping mug", 3);
        shop.tap(By.id("cart-link"));
        shop.tap(By.id("checkout-link"));
        shop.awaitHeading("Checkout");

        shop.type(By.id("name"), "Mia Wagner");
        shop.type(By.id("email"), "mia@example.com");
        shop.type(By.id("phone"), "+49 30 1234567");
        shop.type(By.id("postcode"), "10115");
        shop.tapAfterTyping(By.id("place-order"));

        shop.awaitHeading("Order confirmed");
        assertTrue(shop.visible(By.id("order-number")).getText().matches("\\d{6}"));
        assertEquals(shop.cartCount(), 0);
    }
}
