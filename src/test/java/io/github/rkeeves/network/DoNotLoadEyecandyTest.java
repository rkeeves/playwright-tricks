package io.github.rkeeves.network;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoNotLoadEyecandyTest {

    /**
     * What's the goal?
     * Demonstrate usage of Route abort.
     *
     * What does it do?
     * We visit a book store app.
     * It fetches a lot of data, but it also fetches images.
     * In this simple example we just abort the loading of the logo image.
     * It can be beneficial to not load eyecandy during functional tests.
     */
    @Test
    void example() {
        page.route("**/Toolsqa.{png,jpg,jpeg}", Route::abort);
        page.navigate("https://demoqa.com/books");
        final var naturalWidth = page.locator("header img").evaluate("e => e.naturalWidth");
        final var naturalHeight = page.locator("header img").evaluate("e => e.naturalHeight");
        assertEquals(0, naturalWidth);
        assertEquals(0, naturalHeight);
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