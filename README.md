# playwright-e2e-framework

Unified end-to-end web testing framework built on the Playwright Java API and the Page Object Model (POM). The suite drives a bundled Spring Boot "product store" application on a random local port, and exercises the store and session-driven login flows through a real headless Chromium browser.

Playwright version: **1.49.0** | Java 21 | Spring Boot 3.3.5 | Maven

## Architecture

The framework follows the standard POM pattern: every rendered page is wrapped by a page object that exposes typed locators and reusable actions, while tests only express user intent and assertions.

```
+--------------------------------------------------------------+
|  Test classes (JUnit 5)                                       |
|  StoreFlowE2ETest, LoginFlowE2ETest                          |
+-------------------------------^------------------------------+
                                | extends
+-------------------------------|------------------------------+
|  BaseE2ETest                                                  |
|  - launches Playwright + headless Chromium (@BeforeAll)       |
|  - @SpringBootTest(RANDOM_PORT) + @LocalServerPort            |
|  - resets app state via Playwright APIRequestContext          |
|  - builds BrowserContext + Page per test (@BeforeEach)        |
+-------------------------------^------------------------------+
                                | uses
+-------------------------------|------------------------------+
|  Page Objects                                                 |
|  StorePage, LoginPage                                         |
|  - locators -> Playwright auto-waiting                        |
|  - actions  -> typed methods (fill, click, submit)            |
|  - navigation methods return the next page object             |
+-------------------------------+-------------------------------+
                                | drives
+-------------------------------v-------------------------------+
|  System under test: bundled Spring Boot app                  |
|  static pages + REST endpoints (in-memory store, sessions)    |
+--------------------------------------------------------------+
```

Key design points:

- **Real DOM assertions** — the suite uses `PlaywrightAssertions.assertThat(...)` (`hasText`, `hasCount`, `isEmpty`, `isVisible`, `hasURL`) which poll until the expected state is reached.
- **Auto-waiting** — every locator action (`fill`, `click`) built on Playwright's actionability checks; no sleeps.
- **Page object navigation** — e.g. `store.goToLoginPage()` returns a `LoginPage`, and `login.login("admin", "admin123")` returns the `StorePage` again after the redirect.
- **Isolated context per test** — a fresh `BrowserContext` (and therefore fresh cookies/session) per test method, plus a state reset of the in-memory catalog through an `APIRequestContext` call.
- **Local server** — `@SpringBootTest(webEnvironment = RANDOM_PORT)` boots the target app on an ephemeral port; `@LocalServerPort` exposes it to the browser.

## System under test

A minimal Spring Boot web app bundled inside this repository:

| Route                     | Behavior                                                    |
|---------------------------|-------------------------------------------------------------|
| `GET /`                   | redirect to the store page (`index.html`)                   |
| `GET /products`           | JSON list of products (in-memory catalog)                   |
| `POST /products`          | adds a product (`{"name": "..."}`), validates non-blank     |
| `DELETE /products`        | clears the catalog (used for test state reset)              |
| `GET /login`              | shows the sign-in form                                      |
| `POST /login`             | form login; `admin` / `admin123` starts a session           |
| `GET /api/me`             | current session user (drives the "Welcome, admin" banner)   |
| `/index.html`, `/login.html` | static pages served from `src/main/resources/static`     |

## Prerequisites

- JDK 21 or newer
- Maven 3.9+

## Installing Playwright browsers

Playwright needs its Chromium binary before tests can run. The Playwright Maven plugin is no longer published to Maven Central, so this project pins the `exec-maven-plugin` and drives the documented Playwright CLI (`com.microsoft.playwright.CLI`) instead. The command is reproducible because the plugin version is pinned in `pom.xml`.

```bash
# Windows / PowerShell
mvn -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.classpathScope=test -D exec.args="install chromium"
```

```bash
# Linux / macOS (includes OS library dependencies for CI)
mvn -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.classpathScope=test -D exec.args="install --with-deps chromium"
```

Browsers are cached under `%USERPROFILE%\AppData\Local\ms-playwright` on Windows and `~/.cache/ms-playwright` on Linux/macOS. Re-run the command after upgrading the Playwright version.

## Running the tests

```bash
mvn -B test
```

Maven compiles the target app, boots it on a random port, installs/loads Playwright, launches headless Chromium, and executes the suite. Test reports are written to `target/surefire-reports`.

### Test list

`StoreFlowE2ETest` (product store catalog):

- `storePageRendersEmptyProductList` — page shell renders, list starts empty
- `addingAProductShowsItInTheList` — add through the form, item appears in the DOM
- `addingAProductClearsTheInputField` — input is emptied after an add
- `multipleAddsPersistProductsInOrder` — three adds keep order and count
- `productsSurviveAPageReload` — catalog round-trips through the backend
- `clearingTheListRemovesAllProducts` — Clear List empties the DOM list
- `signInLinkNavigatesToTheLoginPage` — POM navigation store → login

`LoginFlowE2ETest` (session-driven login):

- `validCredentialsSignInAndShowWelcomeBanner` — `admin/admin123` lands on the store with a welcome banner
- `invalidCredentialsAreRejectedWithAnError` — wrong password keeps the error visible on the login page
- `signingInFromTheStorePageCompletesTheFullNavigationFlow` — store → login → authenticated store (POM round trip)

## Project layout

```
src/main/java/com/sangiya/e2e/          target application
  E2eAppApplication.java                 Spring Boot launcher
  controller/StoreController.java        catalog, login and session endpoints
  model/Product.java                     catalog item
  model/AddProductRequest.java           POST /products payload
  service/ProductStore.java              thread-safe in-memory catalog
src/main/resources/
  application.yml                        minimal server config
  static/index.html                      store page (subject under test)
  static/login.html                      login page (subject under test)
src/test/java/com/sangiya/e2e/           test framework
  BaseE2ETest.java                       Playwright + Spring test bootstrap
  pages/StorePage.java                   store page object
  pages/LoginPage.java                   login page object
  StoreFlowE2ETest.java                  catalog flows
  LoginFlowE2ETest.java                  login flows
.github/workflows/ci.yml                 CI: browsers + mvn -B test
```

## CI

`.github/workflows/ci.yml` runs on `ubuntu-latest` with JDK 21. It installs Chromium plus the required OS libraries (`install --with-deps chromium`), then executes `mvn -B test`.