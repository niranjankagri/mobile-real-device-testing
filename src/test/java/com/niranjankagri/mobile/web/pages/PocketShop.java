package com.niranjankagri.mobile.web.pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * The entry point to the shop: opens a page by its address and returns its page object. From there, tests move
 * through the shop with the pages' own methods (each action returns the page it leads to).
 */
public final class PocketShop {

    private final WebDriver driver;
    private final String baseUrl;

    /** baseUrl is the shop's address as the browser sees it (localhost, or bs-local.com on BrowserStack). */
    public PocketShop(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    /** Opens the product list. */
    public ProductListPage openProductList() {
        load(ProductListPage.PATH);
        return new ProductListPage(driver).await();
    }

    /** A product page by id: "mug", "lamp" or "bottle". */
    public ProductPage openProduct(String productId) {
        load("product.html?id=" + productId);
        return new ProductPage(driver).await();
    }

    /** Opens the cart directly by its address. */
    public CartPage openCart() {
        load(CartPage.PATH);
        return new CartPage(driver).await();
    }

    /** Opens the checkout directly by its address. */
    public CheckoutPage openCheckout() {
        load(CheckoutPage.PATH);
        return new CheckoutPage(driver).await();
    }

    /**
     * Starts each test with an empty cart. The cart is in localStorage, which belongs to the site, so a page of the
     * site must be open to clear it.
     */
    public ProductListPage startEmpty() {
        load(ProductListPage.PATH);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear()");
        return openProductList();
    }

    /** Opens an address of the shop; the page object's await() then waits until that page is ready. */
    private void load(String path) {
        driver.get(baseUrl + path);
    }
}
