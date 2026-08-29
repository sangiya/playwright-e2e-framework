package com.sangiya.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage {

    private final Page page;

    public LoginPage(Page page) {
        this.page = page;
    }

    public Locator usernameInput() {
        return page.locator("#username");
    }

    public Locator passwordInput() {
        return page.locator("#password");
    }

    public Locator submitButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in"));
    }

    public Locator errorMessage() {
        return page.locator("#login-error");
    }

    public void enterCredentials(String username, String password) {
        usernameInput().fill(username);
        passwordInput().fill(password);
    }

    public void submit() {
        submitButton().click();
    }

    public StorePage login(String username, String password) {
        enterCredentials(username, password);
        submit();
        return new StorePage(page);
    }
}