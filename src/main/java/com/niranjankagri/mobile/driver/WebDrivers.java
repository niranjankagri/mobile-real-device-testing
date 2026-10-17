package com.niranjankagri.mobile.driver;

import com.niranjankagri.mobile.config.BrowserStack;
import com.niranjankagri.mobile.config.Target;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/** Creates the browser session for a target. */
public final class WebDrivers {

    /** The emulated phone: an iPhone 15 sized screen. */
    private static final Map<String, Object> PHONE_METRICS = Map.of("width", 390, "height", 844, "pixelRatio", 3.0, "touch", true);
    private static final String IPHONE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 18_0 like Mac OS X) AppleWebKit/605.1.15 "
            + "(KHTML, like Gecko) Version/18.0 Mobile/15E148 Safari/604.1";

    private WebDrivers() {}

    /** Local Chrome emulating a phone, or the browser on a real phone at BrowserStack. */
    public static WebDriver create(Target target, String sessionName) {
        return switch (target) {
            case LOCAL -> localPhone();
            case IPHONE -> remote("safari", "iPhone 16", "18", sessionName);
            case ANDROID -> remote("chrome", "Samsung Galaxy S24", "14.0", sessionName);
        };
    }

    /** Chrome on this machine with mobile emulation: screen size, pixel ratio, touch and a phone user agent. */
    private static WebDriver localPhone() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", Map.of("deviceMetrics", PHONE_METRICS, "userAgent", IPHONE_USER_AGENT));
        if (Boolean.parseBoolean(System.getProperty("headless", String.valueOf(System.getenv("CI") != null)))) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }

    /** A browser on a real phone at BrowserStack, connected to this run's tunnel. */
    private static WebDriver remote(String browser, String device, String osVersion, String sessionName) {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("browserName", browser);
        Map<String, Object> options = BrowserStack.sessionOptions(sessionName);
        options.put("deviceName", device);
        options.put("osVersion", osVersion);
        options.put("realMobile", "true"); // a physical phone, not an emulator or simulator
        // Use this run's tunnel, so bs-local.com reaches the site on this machine
        options.put("local", "true");
        options.put("localIdentifier", BrowserStack.LOCAL_IDENTIFIER);
        capabilities.setCapability("bstack:options", options);
        return new RemoteWebDriver(BrowserStack.HUB, capabilities);
    }

    /**
     * Tells BrowserStack whether the session passed, so the dashboard shows it (BrowserStack only sees commands,
     * not test results). Does nothing for local runs.
     */
    public static void reportStatus(WebDriver driver, Target target, boolean passed, String reason) {
        if (!target.onBrowserStack()) {
            return;
        }
        // The reason goes inside a JSON string: remove what would break it
        String safeReason = reason.replace("\\", " ").replace("\"", "'");
        ((JavascriptExecutor) driver).executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": "
                + "{\"status\": \"" + (passed ? "passed" : "failed") + "\", \"reason\": \"" + safeReason + "\"}}");
    }
}
