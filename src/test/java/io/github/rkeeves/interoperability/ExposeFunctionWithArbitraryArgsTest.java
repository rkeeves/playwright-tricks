package io.github.rkeeves.interoperability;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.FunctionCallback;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExposeFunctionWithArbitraryArgsTest {

    /**
     * What's the goal?
     * Demonstrate the usage of functions.
     *
     * What does it do?
     * We register basically a callback and call it from the client code.
     */
    @Test
    void example() {
        final var callback = new FunctionCallbackWithContextAndUnsafeCasts();

        final var targetId = "target";
        final var functionName = "callJava";
        final var arg0 = "Message";
        final var arg1 = 1;
        final var arg2 = true;

        page.exposeFunction(
                functionName,
                callback);
        page.setContent("" +
                "<script>" +
                String.format("  document.addEventListener('click', event => window.%s('%s', %d, %s));", functionName, arg0, arg1, arg2) +
                "</script>" +
                "<div id='" + targetId + "'>Target</div>");
        page.locator("#target").click();

        assertEquals(arg0, callback.getArg0());
        assertEquals(arg1, callback.getArg1());
        assertEquals(arg2, callback.getArg2());
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


    static class FunctionCallbackWithContextAndUnsafeCasts implements FunctionCallback {

        @Getter
        Object arg0;

        @Getter
        Object arg1;

        @Getter
        Object arg2;

        @Override
        public Object call(Object... objects) {
            arg0 = objects[0];
            arg1 = objects[1];
            arg2 = objects[2];
            return null;
        }
    }
}
