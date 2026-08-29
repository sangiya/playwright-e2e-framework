package com.sangiya.e2e;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.sangiya.e2e.pages.LoginPage;
import com.sangiya.e2e.pages.StorePage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseE2ETest {

    protected static final int VIEWPORT_WIDTH = 1280;
    protected static final int VIEWPORT_HEIGHT = 720;

    protected static Playwright playwright;
    protected static Browser browser;

    @LocalServerPort
    protected int port;

    protected String baseUrl;
    protected BrowserContext context;
    protected Page page;
    protected APIRequestContext api;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        api = playwright.request().newContext(new APIRequest.NewContextOptions().setBaseURL(baseUrl));
        api.delete("/products");
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        page = context.newPage();
    }

    @AfterEach
    void tearDown() {
        if (api != null) {
            api.dispose();
        }
        if (context != null) {
            context.close();
        }
    }

    protected StorePage openStore() {
        page.navigate(baseUrl + "/");
        return new StorePage(page);
    }

    protected LoginPage openLogin() {
        page.navigate(baseUrl + "/login");
        return new LoginPage(page);
    }
}