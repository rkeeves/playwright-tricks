package io.github.rkeeves.target;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameIsNotImplementedAsStateVariableTest {

    /**
     * What's the goal?
     * Demonstrate the usage of Frames (Unlike Selenium's switchTo this wasn't implemented as state mutation).
     *
     * What does it do?
     * We hop through nested dialogs.
     * Each has an iframe.
     * Instead of mutating our whole state via switchTo like in Selenium,
     * we explicitly target frames in actions.
     * Due to the fact that "current frame" is not a state variable,
     * it requires no state management.
     * Aka you can't mess things up by not setting "current frame" back to the main content.
     */
    @Test
    void example() {
        final var expectedValue = "some value";

        page.navigate("https://www.primefaces.org/showcase/ui/df/nested.xhtml");
        page.locator("*[id='rootform:btn']").click();

        final var dialog1 = new Dialog(page, page.locator("*[id='rootform:btn_dlg']"));
        dialog1
                .mustBeVisible()
                .click("*[id='level1form:level1btn']");

        final var dialog2 = new Dialog(page, page.locator("*[id='level1form:level1btn_dlg']"));
        dialog2
                .mustBeVisible()
                .click("*[id='level2form:level2button']");

        final var dialog3 = new Dialog(page, page.locator("*[id='level2form:level2button_dlg']"));
        dialog3
                .mustBeVisible()
                .fill("*[id='level3form:val']", expectedValue)
                .click("*[id='level3form:j_idt10']");

        dialog3.mustBeHidden();
        dialog2.mustBeHidden();
        dialog1.mustBeHidden();

        assertThat(page.locator(".ui-growl-item p")).hasText(expectedValue);
    }

    static class Dialog {

        private final Page page;
        private final Locator root;
        private final FrameLocator contentIframe;

        public Dialog(Page page, Locator root) {
            this.page = page;
            this.root = root;
            this.contentIframe = root.frameLocator("iframe");
        }

        public Dialog mustBeVisible() {
            assertThat(root).isVisible();
            return this;
        }

        public Dialog mustBeHidden() {
            assertThat(root).isHidden();
            return this;
        }

        public Dialog click(String selector) {
            contentIframe.locator(selector).click();
            return this;
        }

        public Dialog fill(String selector, String value) {
            contentIframe.locator(selector).fill(value);
            return this;
        }
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
