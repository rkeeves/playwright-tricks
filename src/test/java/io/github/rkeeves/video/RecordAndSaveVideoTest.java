package io.github.rkeeves.video;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.BindingCallback;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class RecordAndSaveVideoTest {

    /**
     * What's the goal?
     * Demonstrate the usage of video recording.
     *
     * What does it do?
     * We initialize the browser context with options to capture video footage about the tests.
     */
    @Test
    void example() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("*[data-test='username']").fill("standard_user");
        page.locator("*[data-test='password']").fill("secret_sauce");
        page.locator("*[data-test='login-button']").click();
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
        page.locator("#react-burger-menu-btn").click();
        page.locator(".bm-item-list").getByText("Logout").click();
        assertThat(page).hasURL("https://www.saucedemo.com/");
        page.locator("*[data-test='username']").focus();
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
        context = browser.newContext(new Browser.NewContextOptions()
                        .setRecordVideoDir(Paths.get("./artifact")));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }


    static class BindingCallbackWithContextAndUnsafeCasts implements BindingCallback {

        @Getter
        ElementHandle arg0SentByClientJs;

        @Override
        public Object call(BindingCallback.Source source, Object... objects) {
            arg0SentByClientJs = (ElementHandle) objects[0];
            return null;
        }
    }
}
