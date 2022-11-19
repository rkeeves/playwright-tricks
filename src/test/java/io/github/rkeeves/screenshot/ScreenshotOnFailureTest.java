package io.github.rkeeves.screenshot;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ScreenshotType;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ScreenshotOnFailureTest {

    /**
     * What's the goal?
     * Demonstrate the usage of screenshot in case of test failure.
     *
     * What does it do?
     * We just simply take a screenshot of a page (full) and write it to a file, on test failures.
     * In this example we just create a Watcher class and manually mutate it in the BeforeEach in a naive way.
     * In the real world, you should use JUnit5 Extensions and ExtensionContext's Store (or DI etc.).
     * At that point, you should also probably abstract away the whole lifecycle management
     * of Playwright related resources (Like Playwright, Browser, BrowserContext),
     * and also use ParameterResolver for Page.
     */
    @Test
    void example() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("*[data-test='login-button']").click();
        assertThat(page.locator(".error-message-container")).isHidden();
    }

    static Playwright playwright;

    static Browser browser;

    @RegisterExtension
    static Watcher watcher = new Watcher();

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
        watcher.setPage(page);
    }

    @AfterEach
    void closeContext() {
        context.close();
    }


    static class Watcher implements AfterTestExecutionCallback {

        @Setter
        private Page page;

        @Override
        public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
            if (extensionContext.getExecutionException().isPresent() &&page != null) {
                page.screenshot(new Page.ScreenshotOptions()
                        .setFullPage(true)
                        .setPath(Paths.get("./artifact/on_failure.jpg"))
                        .setType(ScreenshotType.JPEG));
            }
        }
    }
}
