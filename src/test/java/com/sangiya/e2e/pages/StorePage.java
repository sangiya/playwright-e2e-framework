package com.sangiya.e2e.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;

public class StorePage {

    private final Page page;

    public StorePage(Page page) {
        this.page = page;
    }

    public Locator productInput() {
        return page.locator("#product-name");
    }

    public Locator addProductButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Product"));
    }

    public Locator clearListButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Clear List"));
    }

    public Locator productList() {
        return page.locator("#product-list > li");
    }

    public Locator productItem(String name) {
        return productList().filter(new Locator.FilterOptions().setHasText(name));
    }

    public Locator welcomeBanner() {
        return page.locator("#session-banner");
    }

    public Locator signInLink() {
        return page.locator("#signin-link");
    }

    public StorePage addProduct(String name) {
        productInput().fill(name);
        addProductButton().click();
        return this;
    }

    public List<String> productNames() {
        return productList().allTextContents().stream()
                .map(String::strip)
                .toList();
    }

    public StorePage reload() {
        page.reload();
        return this;
    }

    public LoginPage goToLoginPage() {
        signInLink().click();
        return new LoginPage(page);
    }
}