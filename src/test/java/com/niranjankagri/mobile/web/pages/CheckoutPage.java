package com.niranjankagri.mobile.web.pages;

import com.niranjankagri.mobile.web.data.Customer;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** checkout.html: four fields with their error messages, and the Place order button. */
public final class CheckoutPage extends BasePage {

    public static final String PATH = "checkout.html";

    private static final By FORM = By.id("checkout-form");
    private static final By PLACE_ORDER = By.id("place-order");

    /** The fields by their id; each has an error message with the id "<field>-error". */
    public enum Field {
        NAME, EMAIL, PHONE, POSTCODE;

        String id() {
            return name().toLowerCase();
        }
    }

    /** Created by PocketShop, the cart or the menu, never by tests. */
    CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /** Waits until the checkout heading is shown. */
    CheckoutPage await() {
        awaitHeading("Checkout");
        return this;
    }

    /** Types into a field on the phone's keyboard. */
    public CheckoutPage enter(Field field, String text) {
        type(By.id(field.id()), text);
        return this;
    }

    /** Fills in every field with the customer's details (an empty value leaves the field empty). */
    public CheckoutPage enterDetails(Customer customer) {
        return enter(Field.NAME, customer.name())
                .enter(Field.EMAIL, customer.email())
                .enter(Field.PHONE, customer.phone())
                .enter(Field.POSTCODE, customer.postcode());
    }

    /**
     * Taps Place order with valid details. This step failed on the real Android phone until the keyboard was closed
     * first (see {@link BasePage#tapAfterTyping}).
     */
    public ConfirmationPage placeOrder() {
        tapAfterTyping(PLACE_ORDER);
        return new ConfirmationPage(driver).await();
    }

    /** Taps Place order with invalid details: the page stays and shows the errors. */
    public CheckoutPage placeOrderExpectingErrors() {
        tapAfterTyping(PLACE_ORDER);
        return this;
    }

    /** The error message under a field ("" when there is none). */
    public String error(Field field) {
        return visible(By.id(field.id() + "-error")).getText();
    }

    /** Whether the field is marked invalid for screen readers (aria-invalid). */
    public boolean isMarkedInvalid(Field field) {
        return "true".equals(fieldElement(field).getAttribute("aria-invalid"));
    }

    /** The id of the field that has the cursor. */
    public String focusedFieldId() {
        return (String) script("return document.activeElement.id;");
    }

    /** A field, for its keyboard attributes (type, inputmode, autocomplete). */
    public WebElement fieldElement(Field field) {
        return visible(By.id(field.id()));
    }

    /** All inputs of the form, for the font-size check. */
    public List<WebElement> inputs() {
        return visible(FORM).findElements(By.tagName("input"));
    }

    /** The font size the browser really uses for an element, after all CSS rules, in px. */
    public double fontSize(WebElement element) {
        String size = (String) script("return getComputedStyle(arguments[0]).fontSize;", element);
        return Double.parseDouble(size.replace("px", ""));
    }
}
