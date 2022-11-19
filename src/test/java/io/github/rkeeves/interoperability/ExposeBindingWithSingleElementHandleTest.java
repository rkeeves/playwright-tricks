package io.github.rkeeves.interoperability;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExposeBindingWithSingleElementHandleTest {

    /**
     * What's the goal?
     * Demonstrate the usage of binding with ONE ELEMENT HANDLE type arg.
     *
     * What does it do?
     * We register basically a callback and call it from the client code.
     * The 'new Page.ExposeBindingOptions().setHandle(true)' means 2 things:
     * - with this option your binding can have ONLY 1 PARAMETER
     * - args[0] will be converted to ElementHandle for you on Java side
     */
    @Test
    void example() {
        final var callback = new BindingCallbackWithContextAndUnsafeCasts();

        final var targetId = "target";
        final var bindingName = "callJava";

        page.exposeBinding(
                bindingName,
                callback,
                new Page.ExposeBindingOptions().setHandle(true));
        page.setContent("" +
                "<script>" +
                String.format("  document.addEventListener('click', event => window.%s(event.target));", bindingName) +
                "</script>" +
                "<div id='" + targetId + "'>Target</div>");
        page.locator("#target").click();

        final var arg0SentByClientJs = callback.getArg0SentByClientJs();
        assertNotNull(arg0SentByClientJs);
        assertEquals(targetId, arg0SentByClientJs.getAttribute("id"));
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


    static class BindingCallbackWithContextAndUnsafeCasts implements BindingCallback {

        @Getter
        ElementHandle arg0SentByClientJs;

        @Override
        public Object call(Source source, Object... objects) {
            arg0SentByClientJs = (ElementHandle) objects[0];
            return null;
        }
    }
}
