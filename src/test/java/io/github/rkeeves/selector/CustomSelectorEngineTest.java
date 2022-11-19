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

public class CustomSelectorEngineTest {

    /**
     * What's the goal?
     * Demonstrate the usage of custom selector engines.
     *
     * What does it do?
     * It uses some UI libraries built-in client side js (which in turn uses jQuery...sigh),
     * to pick out a DOM element based on JSF generated id.
     * This is a really contrived example, wouldn't advise it in real world scenarios.
     */
    @Test
    void example() {
        playwright.selectors().register("widget", SELECTOR_JSF_WIDGET_ID);
        page.navigate("https://www.primefaces.org/showcase/ui/ajax/dropdown.xhtml");
        final var country = page.locator("widget=j_idt343:country");
        assertThat(country).isVisible();
        assertThat(country).hasAttribute("aria-expanded", "false");
        country.locator("label").click();
        assertThat(country).hasAttribute("aria-expanded", "true");
    }

    private static final String SELECTOR_JSF_WIDGET_ID = "{\n" +
            "  query(root, selector) {\n" +
            "    return window.PrimeFaces.getWidgetById(selector).jq[0];\n" +
            "  },\n" +
            "\n" +
            "  queryAll(root, selector) {\n" +
            "    return window.PrimeFaces.getWidgetById(selector).jq;\n" +
            "  }\n" +
            "}";

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
