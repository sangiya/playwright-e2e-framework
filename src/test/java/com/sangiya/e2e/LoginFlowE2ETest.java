package com.sangiya.e2e;

import com.sangiya.e2e.pages.LoginPage;
import com.sangiya.e2e.pages.StorePage;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class LoginFlowE2ETest extends BaseE2ETest {

    @Test
    void validCredentialsSignInAndShowWelcomeBanner() {
        LoginPage login = openLogin();

        StorePage store = login.login("admin", "admin123");

        assertThat(page).hasURL(Pattern.compile(".*/index\\.html"));
        assertThat(store.welcomeBanner()).hasText("Welcome, admin");
    }

    @Test
    void invalidCredentialsAreRejectedWithAnError() {
        LoginPage login = openLogin();

        login.enterCredentials("admin", "wrong-password");
        login.submit();

        assertThat(page).hasURL(Pattern.compile(".*/login\\.html(?:;jsessionid=[0-9A-F]+)?\\?error=1"));
        assertThat(login.errorMessage()).isVisible();
        assertThat(login.errorMessage()).hasText("Invalid username or password");
    }

    @Test
    void signingInFromTheStorePageCompletesTheFullNavigationFlow() {
        StorePage store = openStore();

        LoginPage login = store.goToLoginPage();
        StorePage authenticatedStore = login.login("admin", "admin123");

        assertThat(page).hasURL(Pattern.compile(".*/index\\.html"));
        assertThat(authenticatedStore.welcomeBanner()).hasText("Welcome, admin");
        assertThat(authenticatedStore.welcomeBanner()).isVisible();
    }
}