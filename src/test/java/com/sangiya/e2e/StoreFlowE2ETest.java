package com.sangiya.e2e;

import com.sangiya.e2e.pages.LoginPage;
import com.sangiya.e2e.pages.StorePage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class StoreFlowE2ETest extends BaseE2ETest {

    @Test
    void storePageRendersEmptyProductList() {
        StorePage store = openStore();

        assertThat(store.productInput()).isVisible();
        assertThat(store.addProductButton()).isVisible();
        assertThat(store.productList()).hasCount(0);
    }

    @Test
    void addingAProductShowsItInTheList() {
        StorePage store = openStore();

        store.addProduct("Espresso Beans");

        assertThat(store.productList()).hasCount(1);
        assertThat(store.productList()).hasText(new String[]{"Espresso Beans"});
        assertThat(store.productItem("Espresso Beans")).isVisible();
    }

    @Test
    void addingAProductClearsTheInputField() {
        StorePage store = openStore();

        store.productInput().fill("Green Tea");
        store.addProductButton().click();

        assertThat(store.productInput()).isEmpty();
        assertThat(store.productList()).hasText(new String[]{"Green Tea"});
    }

    @Test
    void multipleAddsPersistProductsInOrder() {
        StorePage store = openStore();

        store.addProduct("Dark Roast");
        store.addProduct("Green Tea");
        store.addProduct("Espresso Beans");

        assertThat(store.productList()).hasCount(3);
        assertThat(store.productList()).hasText(new String[]{"Dark Roast", "Green Tea", "Espresso Beans"});
        assertThat(store.productNames()).containsExactly("Dark Roast", "Green Tea", "Espresso Beans");
    }

    @Test
    void productsSurviveAPageReload() {
        StorePage store = openStore();

        store.addProduct("Cold Brew");
        store.reload();

        assertThat(store.productList()).hasText(new String[]{"Cold Brew"});
    }

    @Test
    void clearingTheListRemovesAllProducts() {
        StorePage store = openStore();
        store.addProduct("Dark Roast");
        store.addProduct("Green Tea");

        store.clearListButton().click();

        assertThat(store.productList()).hasCount(0);
    }

    @Test
    void signInLinkNavigatesToTheLoginPage() {
        StorePage store = openStore();

        LoginPage login = store.goToLoginPage();

        assertThat(login.usernameInput()).isVisible();
        assertThat(login.passwordInput()).isVisible();
        assertThat(login.submitButton()).isVisible();
    }
}