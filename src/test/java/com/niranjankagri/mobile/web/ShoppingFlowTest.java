package com.niranjankagri.mobile.web;

import static com.niranjankagri.mobile.web.pages.CheckoutPage.Field.EMAIL;
import static com.niranjankagri.mobile.web.pages.CheckoutPage.Field.PHONE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.niranjankagri.mobile.web.data.Customer;
import com.niranjankagri.mobile.web.pages.CartPage;
import com.niranjankagri.mobile.web.pages.CheckoutPage;
import com.niranjankagri.mobile.web.pages.ConfirmationPage;
import com.niranjankagri.mobile.web.pages.ProductPage;
import org.testng.annotations.Test;

/** The shop's main journey, done with taps and the on-screen keyboard. */
public class ShoppingFlowTest extends WebTestBase {

    @Test(description = "[TC-106] Adding a product with quantity 2 updates the cart badge and the cart")
    public void tc106_addingToCartUpdatesTheBadge() {
        ProductPage product = shop.openProductList().openProduct("Folding desk lamp").setQuantity(2).addToCart();
        assertEquals(product.header().cartCount(), 2);

        // The cart page shows the line and the total: 2 x 34.00
        CartPage cart = product.header().openCart();
        assertEquals(cart.line("lamp"), "2 × Folding desk lamp\n€ 68.00");
        assertEquals(cart.total(), "€ 68.00");
    }

    @Test(description = "[TC-107] The cart survives a reload: it is kept on the device")
    public void tc107_cartSurvivesAReload() {
        shop.openProductList().openProduct("Steel water bottle").addToCart();
        // Loading the page again from the server: the cart must come back from localStorage
        CartPage cart = shop.openCart();
        assertEquals(cart.header().cartCount(), 1);
        assertEquals(cart.total(), "€ 19.90");
    }

    @Test(description = "[TC-108] Checkout with invalid details shows the errors and moves the cursor to the first one")
    public void tc108_invalidCheckoutShowsErrors() {
        CheckoutPage checkout = shop.openCheckout()
                .enterDetails(Customer.withInvalidEmailAndMissingFields())
                .placeOrderExpectingErrors();

        // One message per invalid field, the field marked for screen readers, and the cursor on the first error
        assertEquals(checkout.error(EMAIL), "Enter an email like name@example.com");
        assertEquals(checkout.error(PHONE), "Enter a phone number");
        assertTrue(checkout.isMarkedInvalid(EMAIL));
        assertEquals(checkout.focusedFieldId(), "email", "focus should move to the first invalid field");
    }

    @Test(description = "[TC-109] A complete purchase on the phone: add, cart, checkout, confirmation, empty cart")
    public void tc109_completePurchase() {
        // Add 3 mugs, then go to the checkout through the cart, with taps only
        CheckoutPage checkout = shop.openProductList()
                .openProduct("Enamel camping mug").setQuantity(3).addToCart()
                .header().openCart()
                .checkout();

        // Valid details typed on the phone's keyboard; placeOrder waits for the confirmation page
        ConfirmationPage confirmation = checkout.enterDetails(Customer.valid()).placeOrder();

        // A 6-digit order number, and the cart is empty again
        assertTrue(confirmation.orderNumber().matches("\\d{6}"));
        assertEquals(confirmation.header().cartCount(), 0);
    }
}
