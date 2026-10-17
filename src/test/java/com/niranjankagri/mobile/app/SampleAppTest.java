package com.niranjankagri.mobile.app;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.niranjankagri.mobile.app.screens.UiElementsScreen;
import org.openqa.selenium.Alert;
import org.testng.annotations.Test;

/**
 * BrowserStack's sample iOS app on a real iPhone, through its screen objects (app.screens). Their locators come from a
 * single discovery session's element tree: no guessing, no minutes spent on trial and error.
 */
public class SampleAppTest extends AppTestBase {

    @Test(description = "The app starts on the UI Elements tab, with that tab selected")
    public void startsOnTheUiElementsTab() {
        assertEquals(app.title(), "UI Elements");
        assertTrue(app.tabBar().isUiElementsSelected());
    }

    @Test(description = "Text typed on the real keyboard is shown back by the app")
    public void typedTextIsShownBack() {
        String email = "hello@browserstack.com";
        assertEquals(app.enterText(email).shownText(email), email);
    }

    @Test(description = "The Alert button opens a native iOS alert, which can be read and dismissed")
    public void alertOpensAndCloses() {
        Alert alert = app.openAlert();
        assertFalse(alert.getText().isBlank(), "the alert has no text");
        alert.accept();
        // Dismissed: the screen behind is usable again
        assertTrue(app.isUsable());
    }

    @Test(description = "The Web View tab shows web content inside the native app")
    public void webViewTabShowsWebContent() {
        app.tabBar().openWebView().awaitPageLoaded();
        assertTrue(app.tabBar().isWebViewSelected());
    }

    @Test(description = "Switching tabs and back returns to the same native screen")
    public void tabsSwitchBackAndForth() {
        // Away to the web view and back to the native screen
        UiElementsScreen back = app.tabBar().openWebView().tabBar().openUiElements();
        assertEquals(back.title(), "UI Elements");
        assertTrue(back.isUsable());
    }
}
