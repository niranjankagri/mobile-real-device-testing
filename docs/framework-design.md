# Framework design

Why the project is built the way it is. The README says what it does and how to run it.

## 1. Goals

1. Show testing on real phones, not only emulators: a mobile website on real iOS Safari and Android Chrome, and a
   native iOS app on a real iPhone.
2. Get there with a 100-minute trial: develop for free, spend device minutes only to prove and to diagnose.
3. Keep the tests deterministic: an app under test in the repository, reached through a tunnel.

## 2. The parts

| Part | Role | Tools |
|---|---|---|
| PocketShop (`src/main/resources/site`) | the responsive website under test, mobile first | HTML, CSS, plain JavaScript |
| `SiteServer` | serves it on port 4330 | JDK `HttpServer` |
| `Target`, `WebDrivers` | local emulation or a real phone browser | Selenium 4, Chrome mobile emulation, BrowserStack Automate |
| `Tunnel` | lets BrowserStack's devices open `bs-local.com:4330` on this machine | BrowserStack Local (Java binding) |
| `AppDrivers` | native app sessions on a real iPhone | Appium Java client, XCUITest, BrowserStack App Automate |
| page objects (`web/pages`) | one class per page plus the `Header` component; `PocketShop` opens pages by address | Selenium 4 |
| screen objects (`app/screens`) | the native app's screens and its `TabBar` | Appium Java client |
| tests | layout, touch and input, shopping flow, native app; no locators, only page and screen methods | TestNG |

### Design patterns

- **Page Object Model.** Each page class keeps its locators private and offers what a user does (`setQuantity`,
  `addToCart`, `enterDetails`, `placeOrder`) and what a test reads (`total`, `error(EMAIL)`, `orderNumber`).
  `BasePage` holds the shared parts: waits, taps, typing, `tapAfterTyping`, and measurements of the laid-out page
  (`box`, `viewportWidth`, `sidewaysOverflow`).
- **Components.** The header is on every page, so it is one `Header` object reached with `page.header()`; the native
  app's tab bar is a `TabBar`.
- **Fluent navigation.** An action that leads to another page returns that page object after calling its
  `await()` waits until the page is ready (its heading). Tests cannot read the previous page by mistake.
- **Factory.** `WebDrivers.create(target)` returns emulation or a real phone browser; tests never branch on the target.
- **Test data object.** `Customer` holds checkout details, with named sample customers.
- **Template method.** `WebTestBase` and `AppTestBase` fix the life cycle: one session per suite, a reset before each
  test, a screenshot on failure, the result sent to BrowserStack.

## 3. One suite, three targets

- `-Dtarget=local` (default): Chrome with mobile emulation (390 × 844 CSS px, pixel ratio 3, touch, an iPhone user
  agent). Free, runs in CI on every push, catches layout and logic problems fast.
- `-Dtarget=iphone`: Safari on a real iPhone 16 (iOS 18). `-Dtarget=android`: Chrome on a real Samsung Galaxy S24
  (Android 14). Both through BrowserStack Automate and the tunnel.
- The base URL host differs: `localhost` locally, `bs-local.com` on BrowserStack. iOS Safari does not send `localhost`
  through the tunnel; `bs-local.com` is BrowserStack's name for "this machine via the tunnel".

## 4. Saving minutes

- **One session per suite.** `@BeforeSuite` starts the browser; before each test the shop is reset by clearing
  localStorage (where the cart lives). Session start-up on a real device takes 10 to 20 seconds; paid once.
- **Develop on emulation.** All 13 web tests were written and green locally before the first device run.
- **Discovery instead of guessing.** For the native app, one session saved the element tree of each tab to XML files;
  the locators were written from them. The suite then passed on its first device run.
- **Probe instead of guessing.** When the Android checkout failed, a single-test run with a small probe (which element
  received the click, did a submit fire, was the keyboard open) found the cause in about 50 seconds.
- **Idle timeout 120 s** in the session options; **manual CI jobs** for real devices.

## 5. Mobile web checks

- **Device facts:** `window.innerWidth` at most 430 and `navigator.maxTouchPoints` above 0, so a run on the wrong
  target cannot pass silently.
- **Layout:** the viewport meta tag; one column (all cards share one x position); `scrollWidth - clientWidth` is 0 on
  every page; the menu button (`aria-expanded`) instead of an inline menu.
- **Touch:** every button at least 44 × 44 CSS px, measured with `getBoundingClientRect`.
- **Keyboards:** `type`, `inputmode` and `autocomplete` per field decide the on-screen keyboard and autofill.
- **iOS zoom on focus:** inputs below 16 px make iOS Safari zoom in and stay zoomed; computed font sizes are checked.
- **Thumb reach:** the sticky action button sits in the lower half of the screen.
- **Flow:** quantity, cart badge, persistence across reloads, validation with focus on the first error, a complete
  purchase that clears the cart.

## 6. The Android keyboard finding

On the real Samsung Galaxy S24, the complete-purchase test stayed on the checkout page with a valid form. The
screenshot alone suggested a timing problem; a probe showed otherwise: the postcode field still had focus, the
viewport was 646 px high with the keyboard open, the button was fully visible at y 548, and the tap landed on `<html>`
with no submit event. `BasePage.tapAfterTyping` (used by `CheckoutPage.placeOrder`) now blurs the field (the keyboard closes), waits until the button's position is
the same in two measurements 300 ms apart, and then taps. Local emulation and iPhone Safari never showed it.

The stricter "wait for the expected heading" check, written for the first (wrong) guess, stays because it is the
correct way to assert after navigation anyway.

## 7. Native app

- BrowserStack's sample app (`com.browserstack.Sample-iOS`): tabs UI Elements, Web View, Local Testing.
- Accessibility ids (`Text Button`, `Text Input`, `Text Output`, `Alert`, tab names) and iOS class chains
  (`**/XCUIElementTypeNavigationBar`, links inside the web view).
- The app is restarted (`terminateApp` + `activateApp`) before each test inside one session.
- The web view's content (browserstack.com) changes over time, so the test checks structure (a web view with links),
  not wording.
- On failure: screenshot and the element tree, so a locator can be fixed without another run.

## 8. Signing: why not the Sauce Labs demo app

Real iPhones only run signed apps. BrowserStack re-signs uploads with its own certificates, which requires an IPA built
with a development or ad-hoc provisioning profile. Uploads of the Sauce Labs demo app's IPAs 2.1.2, 2.2.2 and 2.3.0
were all rejected; the 2.3.0 IPA, inspected, contains no `embedded.mobileprovision`. BrowserStack's sample app
contains one. In a real project the team's own
app is built for testing with such a profile.

## 9. Reporting

- `setSessionStatus` through the `browserstack_executor` script: the dashboard shows passed or failed per session
  (BrowserStack only sees commands, not assertions).
- Sessions are grouped by build name (`-DbsBuild`) under the project "mobile-real-device-testing"; videos and logs are
  in the dashboard. Session durations come from the BrowserStack REST API.
- The OS minor version is chosen by BrowserStack: the same "iPhone 16, iOS 18" request ran on 18.5 and on 18.6.

## 10. What a real setup adds

- A device matrix chosen from analytics (the devices and OS versions customers really use), run in parallel.
- The team's own app builds signed for testing, uploaded by CI.
- Visual checks per device, and performance (page load on a real 4G profile) with BrowserStack's network throttling.
- Scheduled real-device runs (nightly) rather than per push, to balance cost and feedback time.
