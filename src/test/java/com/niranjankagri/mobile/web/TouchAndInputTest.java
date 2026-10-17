package com.niranjankagri.mobile.web;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/** Fingers and on-screen keyboards: target sizes, keyboard types, and the iOS zoom-on-focus trap. */
public class TouchAndInputTest extends WebTestBase {

    @Test(description = "Buttons are at least 44 x 44 CSS px, big enough for a finger")
    public void touchTargetsAreBigEnough() {
        shop.open("product.html?id=mug");
        for (String id : List.of("menu-button", "cart-link", "less", "more", "add-to-cart")) {
            Map<String, Number> box = shop.box(shop.visible(By.id(id)));
            assertTrue(box.get("width").doubleValue() >= 44 && box.get("height").doubleValue() >= 44,
                    id + " is only " + box.get("width") + " x " + box.get("height") + " px");
        }
    }

    @Test(description = "Each checkout field asks for the right keyboard and offers autofill")
    public void checkoutFieldsChooseTheRightKeyboard() {
        shop.open("checkout.html");
        // type/inputmode decide the keyboard on a phone; autocomplete lets the browser fill saved details.
        assertField("email", "email", null, "email");
        assertField("phone", "tel", null, "tel");
        assertField("postcode", "text", "numeric", "postal-code");
        assertField("name", "text", null, "name");
    }

    @Test(description = "Inputs use at least 16 px text: iOS Safari zooms into smaller fields on focus and stays zoomed")
    public void inputsAreLargeEnoughToAvoidZoomOnFocus() {
        shop.open("checkout.html");
        for (WebElement input : shop.visible(By.id("checkout-form")).findElements(By.tagName("input"))) {
            String size = (String) shop.script("return getComputedStyle(arguments[0]).fontSize;", input);
            assertTrue(Double.parseDouble(size.replace("px", "")) >= 16, input.getAttribute("id") + " has font-size " + size);
        }
    }

    @Test(description = "The add-to-cart button stays on screen at the bottom, within thumb reach")
    public void addToCartStaysWithinThumbReach() {
        shop.open("product.html?id=lamp");
        Map<String, Number> button = shop.box(shop.visible(By.id("add-to-cart")));
        Number viewportHeight = (Number) shop.script("return window.innerHeight;");
        double bottom = button.get("y").doubleValue() + button.get("height").doubleValue();
        assertTrue(bottom <= viewportHeight.doubleValue(), "the button ends below the screen");
        assertTrue(button.get("y").doubleValue() > viewportHeight.doubleValue() / 2, "the button is not in the lower half");
    }

    private void assertField(String id, String type, String inputMode, String autocomplete) {
        WebElement field = shop.visible(By.id(id));
        assertEquals(field.getAttribute("type"), type, id + " type");
        assertEquals(field.getAttribute("inputmode"), inputMode, id + " inputmode");
        assertEquals(field.getAttribute("autocomplete"), autocomplete, id + " autocomplete");
    }
}
