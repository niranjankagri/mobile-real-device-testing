# Manual test cases

The test cases this project automates on real phones (BrowserStack) and in Chrome's mobile emulation,
written as a tester would run them by hand with a phone. Every case has an ID; the TestNG method that
covers it starts with the same ID (`tc109_completePurchase`) and so does its description
(`[TC-109] A complete purchase on the phone ...`), so a failure in the report leads straight to the
case below.

| ID range | Area | Automated in |
|---|---|---|
| TC-101 to TC-113 | Pocket Shop in the phone's browser (layout, shopping, touch and input) | `web/*Test.java` (suite `testng-web.xml`) |
| TC-114 to TC-118 | BrowserStack's sample iOS app (native) | `app/SampleAppTest.java` (suite `testng-app.xml`) |

## Setup

- Web cases: a phone (iPhone with Safari, Android with Chrome) or Chrome DevTools device mode
  (≤ 430 px wide, touch on). The tests start the Pocket Shop themselves; by hand, serve
  `src/main/resources/site/` with any static server (e.g. `python -m http.server 8000 -d
  src/main/resources/site`) and open it from the phone on the same network.
- Products: Enamel camping mug `€ 12.50`, Folding desk lamp `€ 34.00`, Steel water bottle `€ 19.90`.
- Customer: Mia Wagner, `mia@example.com`, `+49 30 1234567`, postcode `10115`.
- App cases: BrowserStack's sample iOS app (`BStackSampleApp.ipa`) on an iPhone.

## Summary

| ID | Title | Type | Priority |
|---|---|---|---|
| TC-101 | The browser is phone-sized and touch-capable | Mobile layout | High |
| TC-102 | The viewport meta tag is set for phones | Mobile layout | High |
| TC-103 | Products are in one column on a phone | Mobile layout | Medium |
| TC-104 | No page scrolls sideways on a phone | Mobile layout | High |
| TC-105 | The menu sits behind a button, opens on tap and navigates | Mobile layout | High |
| TC-106 | Adding a product with quantity 2 updates the badge and the cart | Shopping | High |
| TC-107 | The cart survives a reload | Shopping | Medium |
| TC-108 | Invalid checkout shows errors and moves the cursor to the first | Shopping / Accessibility | High |
| TC-109 | A complete purchase on the phone | Shopping / E2E | High |
| TC-110 | Buttons are at least 44 × 44 CSS px | Touch | Medium |
| TC-111 | Each checkout field brings the right keyboard and offers autofill | Input | Medium |
| TC-112 | Inputs use at least 16 px text (no zoom on focus in iOS Safari) | Input | Medium |
| TC-113 | The add-to-cart button stays within thumb reach | Touch | Low |
| TC-114 | The app starts on the UI Elements tab | Native app / Smoke | High |
| TC-115 | Text typed on the keyboard is shown back | Native app | Medium |
| TC-116 | The Alert button opens a native alert that can be dismissed | Native app | Medium |
| TC-117 | The Web View tab shows web content | Native app | Medium |
| TC-118 | Switching tabs and back returns to the same screen | Native app | Low |

---

## Layout (`web/MobileLayoutTest`)

### TC-101 The browser is phone-sized and touch-capable

| # | Step | Expected result |
|---|---|---|
| 1 | Open the product list on the phone (or emulation) | The page is at most 430 CSS px wide and the browser reports touch support (`navigator.maxTouchPoints` > 0) |

### TC-102 The viewport meta tag is set for phones

| # | Step | Expected result |
|---|---|---|
| 1 | View the page source of the product list | `<meta name="viewport" content="width=device-width, initial-scale=1">`; the page is not shown shrunk to 980 px |

### TC-103 Products are in one column on a phone

| # | Step | Expected result |
|---|---|---|
| 1 | Open the product list | 3 product cards, stacked in one column (all start at the same left edge) |

### TC-104 No page scrolls sideways on a phone

| # | Step | Expected result |
|---|---|---|
| 1 | Open product list, the lamp's product page, cart and checkout; try to swipe sideways on each | None of them scrolls horizontally |

### TC-105 The menu sits behind a button, opens on tap and navigates

| # | Step | Expected result |
|---|---|---|
| 1 | Open the product list | The menu is closed; its button says it is closed (`aria-expanded="false"`) |
| 2 | Tap the menu button | The menu opens; the button says open |
| 3 | Tap **Checkout** in the menu | The Checkout page |

## Shopping (`web/ShoppingFlowTest`)

### TC-106 Adding a product with quantity 2 updates the badge and the cart

| # | Step | Expected result |
|---|---|---|
| 1 | Open "Folding desk lamp", set quantity `2`, **Add to cart** | Cart badge `2` |
| 2 | Open the cart | Line "2 × Folding desk lamp € 68.00"; total `€ 68.00` |

### TC-107 The cart survives a reload

| # | Step | Expected result |
|---|---|---|
| 1 | Add "Steel water bottle", then load the cart page again from the address bar | Badge `1`, total `€ 19.90` (kept in the phone's local storage) |

### TC-108 Invalid checkout shows errors and moves the cursor to the first

| # | Step | Expected result |
|---|---|---|
| 1 | On Checkout enter name Mia Wagner, e-mail `mia-at-example`, empty phone and postcode, place the order | "Enter an email like name@example.com" at the e-mail field (marked invalid for screen readers); "Enter a phone number" at the phone field |
| 2 | Look at the cursor | In the e-mail field (the first invalid field) |

### TC-109 A complete purchase on the phone

| # | Step | Expected result |
|---|---|---|
| 1 | Open "Enamel camping mug", quantity `3`, **Add to cart**, open the cart, **Checkout** (taps only) | Checkout page |
| 2 | Type the customer's details on the phone keyboard, place the order | Confirmation with a 6-digit order number; cart badge empty |

## Touch and input (`web/TouchAndInputTest`)

### TC-110 Buttons are at least 44 × 44 CSS px

| # | Step | Expected result |
|---|---|---|
| 1 | On the mug's product page measure (DevTools) the menu button, cart link, − and + buttons and **Add to cart** | Each at least 44 × 44 CSS px |

### TC-111 Each checkout field brings the right keyboard and offers autofill

| # | Step | Expected result |
|---|---|---|
| 1 | On Checkout tap each field | E-mail: e-mail keyboard (`type=email`, autocomplete `email`); phone: phone pad (`tel`, `tel`); postcode: number pad (`inputmode=numeric`, `postal-code`); name: text (`name`). Saved details are offered for autofill |

### TC-112 Inputs use at least 16 px text

| # | Step | Expected result |
|---|---|---|
| 1 | On an iPhone, tap each checkout field | Safari does not zoom in; every input's font size is at least 16 px |

### TC-113 The add-to-cart button stays within thumb reach

| # | Step | Expected result |
|---|---|---|
| 1 | Open the lamp's product page, do not scroll | **Add to cart** is fully on screen, in the lower half |

## Native app (`app/SampleAppTest`)

### TC-114 The app starts on the UI Elements tab

| # | Step | Expected result |
|---|---|---|
| 1 | Start the sample app | Title "UI Elements"; that tab is selected |

### TC-115 Text typed on the keyboard is shown back

| # | Step | Expected result |
|---|---|---|
| 1 | Tap **Text Button**, type `hello@browserstack.com`, press Return | The app shows `hello@browserstack.com` |

### TC-116 The Alert button opens a native alert that can be dismissed

| # | Step | Expected result |
|---|---|---|
| 1 | Tap **Alert** | A native iOS alert with text |
| 2 | Tap **OK** | The alert closes; the screen is usable again |

### TC-117 The Web View tab shows web content

| # | Step | Expected result |
|---|---|---|
| 1 | Tap the **Web View** tab | A web page loads inside the app; the tab is selected |

### TC-118 Switching tabs and back returns to the same screen

| # | Step | Expected result |
|---|---|---|
| 1 | Go to **Web View**, then back to **UI Elements** | Title "UI Elements"; the screen works |
