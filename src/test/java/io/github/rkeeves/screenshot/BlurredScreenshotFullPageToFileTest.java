package io.github.rkeeves.screenshot;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

public class BlurredScreenshotFullPageToFileTest {

    /**
     * What's the goal?
     * Demonstrate the usage of screenshot with blurred elements.
     *
     * What does it do?
     * We just simply take a screenshot of a page (full) and write it to a file,
     * but some elements are blurred.
     */
    @Test
    void example() {
        page.navigate("https://www.saucedemo.com/");
        page.screenshot(new Page.ScreenshotOptions()
                .setFullPage(true)
                .setMask(List.of(
                        page.locator(".login_logo"),
                        page.locator("*[data-test='username']"),
                        page.locator("*[data-test='password']")
                ))
                .setPath(Paths.get("./artifact/blurred_full_page_screenshot.jpg"))
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
