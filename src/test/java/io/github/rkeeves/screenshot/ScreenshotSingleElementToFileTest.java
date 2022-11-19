package io.github.rkeeves.screenshot;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class ScreenshotSingleElementToFileTest {

    /**
     * What's the goal?
     * Demonstrate the usage of screenshot for elements.
     *
     * What does it do?
     * We just simply take a screenshot of an element and write it to a file.
     */
    @Test
    void example() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("*[data-test='login-button']").click();
        page.locator(".error-message-container")
                .screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get("./artifact/element_screenshot.jpg"))
                .setType(ScreenshotType.JPEG));
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
