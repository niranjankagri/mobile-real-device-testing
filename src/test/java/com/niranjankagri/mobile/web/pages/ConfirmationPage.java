package com.niranjankagri.mobile.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** done.html: the order confirmation. */
public final class ConfirmationPage extends BasePage {

    private static final By ORDER_NUMBER = By.id("order-number");

    ConfirmationPage(WebDriver driver) {
        super(driver);
    }

    ConfirmationPage await() {
        awaitHeading("Order confirmed");
        return this;
    }

    /** The order number shown to the customer. */
    public String orderNumber() {
        return visible(ORDER_NUMBER).getText();
    }
}
