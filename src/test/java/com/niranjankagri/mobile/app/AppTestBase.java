package com.niranjankagri.mobile.app;

import com.niranjankagri.mobile.app.screens.UiElementsScreen;
import com.niranjankagri.mobile.driver.AppDrivers;
import io.appium.java_client.ios.IOSDriver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * One app session on a real iPhone for the whole suite (each session costs start-up time and App Automate minutes).
 * Before each test the app is restarted, so every test begins on the first screen. Tests reach the app only through
 * its screen objects.
 */
public abstract class AppTestBase {

    /** Bundle id of BrowserStack's sample app, as the discovery session reported it. */
    static final String BUNDLE_ID = "com.browserstack.Sample-iOS";

    private static IOSDriver driver;
    private static int failures;

    /** The screen the app shows after the restart; each test starts here. */
    protected UiElementsScreen app;

    @BeforeSuite(alwaysRun = true)
    public void startApp() {
        driver = AppDrivers.iphone("Sample app suite: native iOS on a real iPhone");
    }

    /** terminateApp + activateApp: a cold start of the same installed app, quicker than a new session. */
    @BeforeMethod(alwaysRun = true)
    public void restartApp() {
        driver.terminateApp(BUNDLE_ID);
        driver.activateApp(BUNDLE_ID);
        app = UiElementsScreen.atStart(driver);
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

    /** Sends the result to the BrowserStack dashboard (it only sees commands, not test results) and ends the session. */
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
