package com.niranjankagri.mobile.web;

import static com.niranjankagri.mobile.web.pages.CheckoutPage.Field.EMAIL;
import static com.niranjankagri.mobile.web.pages.CheckoutPage.Field.NAME;
import static com.niranjankagri.mobile.web.pages.CheckoutPage.Field.PHONE;
import static com.niranjankagri.mobile.web.pages.CheckoutPage.Field.POSTCODE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.niranjankagri.mobile.web.pages.CheckoutPage;
import com.niranjankagri.mobile.web.pages.ProductPage;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/** Fingers and on-screen keyboards: target sizes, keyboard types, and the iOS zoom-on-focus trap. */
public class TouchAndInputTest extends WebTestBase {

    @Test(description = "[TC-110] Buttons are at least 44 x 44 CSS px, big enough for a finger")
    public void tc110_touchTargetsAreBigEnough() {
        ProductPage page = shop.openProduct("mug");
        // Every control a finger uses on the product page: header, quantity stepper, main button
        List<WebElement> controls = List.of(page.header().menuButton(), page.header().cartLink(),
                page.lessButton(), page.moreButton(), page.addToCartButton());
        for (WebElement control : controls) {
            Map<String, Number> box = page.box(control);
            assertTrue(box.get("width").doubleValue() >= 44 && box.get("height").doubleValue() >= 44,
                    control.getAttribute("id") + " is only " + box.get("width") + " x " + box.get("height") + " px");
        }
    }

    @Test(description = "[TC-111] Each checkout field asks for the right keyboard and offers autofill")
    public void tc111_checkoutFieldsChooseTheRightKeyboard() {
        CheckoutPage page = shop.openCheckout();
        // type/inputmode decide the keyboard on a phone; autocomplete lets the browser fill saved details.
        assertField(page.fieldElement(EMAIL), "email", null, "email");
        assertField(page.fieldElement(PHONE), "tel", null, "tel");
        assertField(page.fieldElement(POSTCODE), "text", "numeric", "postal-code");
        assertField(page.fieldElement(NAME), "text", null, "name");
    }

    @Test(description = "[TC-112] Inputs use at least 16 px text: iOS Safari zooms into smaller fields on focus and stays zoomed")
    public void tc112_inputsAreLargeEnoughToAvoidZoomOnFocus() {
        CheckoutPage page = shop.openCheckout();
        for (WebElement input : page.inputs()) {
            double size = page.fontSize(input);
            assertTrue(size >= 16, input.getAttribute("id") + " has font-size " + size + " px");
        }
    }

    @Test(description = "[TC-113] The add-to-cart button stays on screen at the bottom, within thumb reach")
    public void tc113_addToCartStaysWithinThumbReach() {
        ProductPage page = shop.openProduct("lamp");
        Map<String, Number> button = page.box(page.addToCartButton());
        double viewportHeight = page.viewportHeight();
        // Fully on screen without scrolling, and in the lower half, where the thumb reaches when holding the phone
        double bottom = button.get("y").doubleValue() + button.get("height").doubleValue();
        assertTrue(bottom <= viewportHeight, "the button ends below the screen");
        assertTrue(button.get("y").doubleValue() > viewportHeight / 2, "the button is not in the lower half");
    }

    /** Checks the three attributes that choose a field's keyboard and autofill; null means "not set". */
    private void assertField(WebElement field, String type, String inputMode, String autocomplete) {
        String id = field.getAttribute("id");
        assertEquals(field.getAttribute("type"), type, id + " type");
        assertEquals(field.getAttribute("inputmode"), inputMode, id + " inputmode");
        assertEquals(field.getAttribute("autocomplete"), autocomplete, id + " autocomplete");
    }
}
