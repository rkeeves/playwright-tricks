package io.github.rkeeves.selector;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CssSelectorTest {

    /**
     * What's the goal?
     * Demonstrate the usage of css selectors.
     *
     * What does it do?
     * ...
     */
    @Test
    void example() {
        final var dataTest = "login-button";
        page.navigate("https://www.saucedemo.com/");
        final var base = page.locator("*[data-test='login-button']");
        assertThat(base).isVisible();

        final var complexCss = page.locator("#login_button_container > .login-box > form > input");
        assertThat(complexCss).hasAttribute("data-test", dataTest);

        final var adjacentSibling = page.locator(".error-message-container + input");
        assertThat(adjacentSibling).hasAttribute("data-test", dataTest);

        final var nthMatch = page.locator(":nth-match(#login_button_container > .login-box > form input, 3)");
        assertThat(nthMatch).hasAttribute("data-test", dataTest);

        final var textIs = page.locator(":text-is('Login')");
        assertThat(textIs).hasAttribute("data-test", dataTest);

        final var nthOfType = page.locator(".login_credentials_wrap-inner > div:nth-of-type(2)");
        assertThat(nthOfType).hasClass("login_password");
    }

    static Playwright playwright;

    static Browser browser;

    BrowserContext context;

    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }
}
