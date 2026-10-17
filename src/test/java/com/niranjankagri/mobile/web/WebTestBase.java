package com.niranjankagri.mobile.web;

import com.niranjankagri.mobile.config.Target;
import com.niranjankagri.mobile.driver.Tunnel;
import com.niranjankagri.mobile.driver.WebDrivers;
import com.niranjankagri.mobile.site.SiteServer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * One browser session for the whole suite. On a real device every session costs start-up time (and BrowserStack
 * minutes), so tests share it and reset the shop's state instead: the cart lives in localStorage, which is cleared
 * before each test.
 */
public abstract class WebTestBase {

    /** Fixed, because the BrowserStack tunnel forwards bs-local.com:4330 to this port on this machine. */
    private static final int PORT = 4330;

    protected static Target target;
    protected static WebDriver driver;
    protected static PocketShop shop;

    private static SiteServer site;
    private static Tunnel tunnel;
    private static int failures;

    @BeforeSuite(alwaysRun = true)
    public void startSuite() {
        target = Target.current();
        site = SiteServer.start(PORT);
        if (target.onBrowserStack()) {
            tunnel = Tunnel.open();
        }
        driver = WebDrivers.create(target, "PocketShop web suite: " + target.description());
        shop = new PocketShop(driver, target.baseUrl(PORT));
    }

    @BeforeMethod(alwaysRun = true)
    public void freshShop() {
        shop.open("index.html");
        ((JavascriptExecutor) driver).executeScript("localStorage.clear()");
        shop.open("index.html");
    }

    @AfterMethod(alwaysRun = true)
    public void keepEvidence(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            failures++;
            Path file = Path.of("reports", "screenshots", target.name().toLowerCase() + "-" + result.getName() + ".png");
            Files.createDirectories(file.getParent());
            Files.write(file, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        }
    }

    @AfterSuite(alwaysRun = true)
    public void endSuite() {
        if (driver != null) {
            try {
                WebDrivers.reportStatus(driver, target, failures == 0, failures == 0 ? "All tests passed" : failures + " test(s) failed");
            } finally {
                driver.quit();
            }
        }
        if (tunnel != null) {
            tunnel.close();
        }
        if (site != null) {
            site.close();
        }
    }
}
