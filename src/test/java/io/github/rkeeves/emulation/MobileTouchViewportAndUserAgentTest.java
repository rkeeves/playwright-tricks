package io.github.rkeeves.emulation;

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

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MobileTouchViewportAndUserAgentTest {

    /**
     *
     * What's the goal?
     * Demonstrate the usage of emulation.
     *
     * What does it do?
     * We initialize the browser context to mimic a Nexus5X device (almost).
     */
    @Test
    void example() {
        page.navigate("https://www.saucedemo.com/");
        page.locator("*[data-test='username']").fill("standard_user");
        page.locator("*[data-test='password']").fill("secret_sauce");
        page.locator("*[data-test='login-button']").tap();
        assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
        page.locator("#react-burger-menu-btn").tap();
        page.locator(".bm-item-list").getByText("Logout").tap();
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
        final var nexus5XContext = new Browser.NewContextOptions()
                .setHasTouch(true)
                .setIsMobile(true)
                .setScreenSize(412, 732)
                .setViewportSize( 412, 732)
                .setDeviceScaleFactor(2.625)
                .setUserAgent("Mozilla/5.0 (Linux; Android 8.0.0; Nexus 5X Build/OPR4.170623.006) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/%s Mobile Safari/537.36");
        context = browser.newContext(nexus5XContext);
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
