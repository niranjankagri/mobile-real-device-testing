package com.niranjankagri.mobile.web;

import com.niranjankagri.mobile.config.Target;
import com.niranjankagri.mobile.driver.Tunnel;
import com.niranjankagri.mobile.driver.WebDrivers;
import com.niranjankagri.mobile.site.SiteServer;
import com.niranjankagri.mobile.web.pages.PocketShop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
 * before each test. Tests reach the shop only through its page objects ({@link PocketShop}).
 */
public abstract class WebTestBase {

    /** Fixed, because the BrowserStack tunnel forwards bs-local.com:4330 to this port on this machine. */
    private static final int PORT = 4330;

    protected static Target target;
    protected static PocketShop shop;

    private static WebDriver driver;
    private static SiteServer site;
    private static Tunnel tunnel;
    private static int failures;

    /**
     * Once per suite: the target from -Dtarget, PocketShop on this machine, the tunnel for real devices, and one
     * browser.
     */
    @BeforeSuite(alwaysRun = true)
    public void startSuite() {
        // Order matters: the site must be running and the tunnel open before a device tries to load a page.
        target = Target.current();
        site = SiteServer.start(PORT);
        if (target.onBrowserStack()) {
            tunnel = Tunnel.open();
        }
        driver = WebDrivers.create(target, "PocketShop web suite: " + target.description());
        shop = new PocketShop(driver, target.baseUrl(PORT));
    }

    /** Every test starts on the product list with an empty cart. */
    @BeforeMethod(alwaysRun = true)
    public void freshShop() {
        shop.startEmpty();
    }

    /** On failure, saves what the phone showed: reports/screenshots/<target>-<test>.png. */
    @AfterMethod(alwaysRun = true)
    public void keepEvidence(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            failures++;
            Path file = Path.of("reports", "screenshots", target.name().toLowerCase() + "-" + result.getName() + ".png");
            Files.createDirectories(file.getParent());
            Files.write(file, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        }
    }

    /**
     * Reports the result to BrowserStack, ends the session (which stops the minutes), then closes the tunnel and the
     * site.
     */
    @AfterSuite(alwaysRun = true)
    public void endSuite() {
        // Close in reverse order. On BrowserStack the session must end (quit) to stop using minutes.
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
