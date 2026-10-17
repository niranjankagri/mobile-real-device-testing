package com.niranjankagri.mobile.app;

import com.niranjankagri.mobile.driver.AppDrivers;
import io.appium.java_client.ios.IOSDriver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * One app session on a real iPhone for the whole suite (each session costs start-up time and App Automate minutes).
 * Before each test the app is restarted, so every test begins on the first tab.
 */
public abstract class AppTestBase {

    /** Bundle id of BrowserStack's sample app, as the discovery session reported it. */
    static final String BUNDLE_ID = "com.browserstack.Sample-iOS";

    protected static IOSDriver driver;
    protected static WebDriverWait wait;
    private static int failures;

    @BeforeSuite(alwaysRun = true)
    public void startApp() {
        driver = AppDrivers.iphone("Sample app suite: native iOS on a real iPhone");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @BeforeMethod(alwaysRun = true)
    public void restartApp() {
        driver.terminateApp(BUNDLE_ID);
        driver.activateApp(BUNDLE_ID);
    }

    @AfterMethod(alwaysRun = true)
    public void keepEvidence(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE) {
            failures++;
            Path dir = Files.createDirectories(Path.of("reports", "app"));
            Files.write(dir.resolve(result.getName() + ".png"), driver.getScreenshotAs(OutputType.BYTES));
            // The element tree at the moment of failure: what Appium could see, for fixing a locator without a new run.
            Files.writeString(dir.resolve(result.getName() + ".xml"), driver.getPageSource());
        }
    }

    @AfterSuite(alwaysRun = true)
    public void endSession() {
        if (driver == null) {
            return;
        }
        try {
            String status = failures == 0 ? "passed" : "failed";
            String reason = failures == 0 ? "All tests passed" : failures + " test(s) failed";
            driver.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": "
                    + "{\"status\": \"" + status + "\", \"reason\": \"" + reason + "\"}}");
        } finally {
            driver.quit();
        }
    }
}
