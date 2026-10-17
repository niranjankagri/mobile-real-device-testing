package com.niranjankagri.mobile.app;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

/**
 * BrowserStack's sample iOS app on a real iPhone. Locators are accessibility ids and iOS class chains, taken from a
 * single discovery session's element tree (see docs/framework-design.md): no guessing, no minutes spent on trial and
 * error.
 */
public class SampleAppTest extends AppTestBase {

    private static final By TEXT_BUTTON = AppiumBy.accessibilityId("Text Button");
    private static final By TEXT_INPUT = AppiumBy.accessibilityId("Text Input");
    private static final By TEXT_OUTPUT = AppiumBy.accessibilityId("Text Output");
    private static final By ALERT_BUTTON = AppiumBy.accessibilityId("Alert");
    private static final By TAB_UI = AppiumBy.accessibilityId("UI Elements");
    private static final By TAB_WEB = AppiumBy.accessibilityId("Web View");
    private static final By NAV_TITLE = AppiumBy.iOSClassChain("**/XCUIElementTypeNavigationBar");
    private static final By WEB_VIEW = AppiumBy.className("XCUIElementTypeWebView");

    @Test(description = "The app starts on the UI Elements tab, with that tab selected")
    public void startsOnTheUiElementsTab() {
        assertEquals(visible(NAV_TITLE).getAttribute("name"), "UI Elements");
        // A selected tab bar button reports value "1"
        assertEquals(visible(TAB_UI).getAttribute("value"), "1");
    }

    @Test(description = "Text typed on the real keyboard is shown back by the app")
    public void typedTextIsShownBack() {
        visible(TEXT_BUTTON).click();
        WebElement input = visible(TEXT_INPUT);
        input.sendKeys("hello@browserstack.com\n");
        wait.until(ExpectedConditions.attributeToBe(TEXT_OUTPUT, "value", "hello@browserstack.com"));
    }

    @Test(description = "The Alert button opens a native iOS alert, which can be read and dismissed")
    public void alertOpensAndCloses() {
        visible(ALERT_BUTTON).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertFalse(alert.getText().isBlank(), "the alert has no text");
        alert.accept();
        // Dismissed: the screen behind is usable again
        assertTrue(visible(ALERT_BUTTON).isDisplayed());
    }

    @Test(description = "The Web View tab shows web content inside the native app")
    public void webViewTabShowsWebContent() {
        visible(TAB_WEB).click();
        visible(WEB_VIEW);
        // The page itself is browserstack.com and changes over time; check the structure, not the wording:
        // a loaded page exposes links to the accessibility tree inside the web view.
        wait.until(d -> !d.findElements(AppiumBy.iOSClassChain("**/XCUIElementTypeWebView/**/XCUIElementTypeLink")).isEmpty());
        assertEquals(visible(TAB_WEB).getAttribute("value"), "1");
    }

    @Test(description = "Switching tabs and back returns to the same native screen")
    public void tabsSwitchBackAndForth() {
        visible(TAB_WEB).click();
        visible(WEB_VIEW);
        visible(TAB_UI).click();
        assertEquals(visible(NAV_TITLE).getAttribute("name"), "UI Elements");
        assertTrue(visible(TEXT_BUTTON).isDisplayed());
    }

    private WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
