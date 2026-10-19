package com.niranjankagri.mobile.web.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** index.html: the product list. */
public final class ProductListPage extends BasePage {

    public static final String PATH = "index.html";

    private static final By CARDS = By.cssSelector("[data-product]");

    /** Created by PocketShop, never by tests. */
    ProductListPage(WebDriver driver) {
        super(driver);
    }

    /** Waits until the Products heading is shown. */
    ProductListPage await() {
        awaitHeading("Products");
        return this;
    }

    /** The product cards, once app.js has rendered them. */
    public List<WebElement> productCards() {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(CARDS, 0));
        return driver.findElements(CARDS);
    }

    /** Taps a product by its name. */
    public ProductPage openProduct(String name) {
        tap(By.linkText(name));
        return new ProductPage(driver).await();
    }
}
