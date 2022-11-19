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

public class XPathTest {

    /**
     * What's the goal?
     * Demonstrate the usage of xpath selectors.
     *
     * What does it do?
     * ...
     */
    @Test
    void example() {
        final var text = "standard_user";
        page.navigate("https://www.saucedemo.com/");
        final var usernameLocatedBySelectorA = page.locator("xpath=//input[@data-test='username']");
        final var usernameLocatedBySelectorB = page.locator("xpath=(//form/div)[1]/input");
        usernameLocatedBySelectorA.fill(text);
        assertThat(usernameLocatedBySelectorA).hasValue(text);
        assertThat(usernameLocatedBySelectorB).hasValue(text);
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
