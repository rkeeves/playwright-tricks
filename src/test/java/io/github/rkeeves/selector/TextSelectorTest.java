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

public class TextSelectorTest {

    /**
     * What's the goal?
     * Demonstrate the usage of text selectors.
     *
     * What does it do?
     * ...
     */
    @Test
    void example() {
        final var id = "root";
        page.setContent("<span id='" + id + "'>  Do not Target Me please  </span>");

        assertThat(page.locator("#root")).isVisible();

        final var nonExactAppliesTrim = page.locator("text=Do not Target Me please");
        assertThat(nonExactAppliesTrim).hasAttribute("id", id);

        final var exactAppliesTrim = page.locator("text='Do not Target Me please'");
        assertThat(exactAppliesTrim).hasAttribute("id", id);

        final var nonExactIsCaseInsensitive = page.locator("text=do not target me please");
        assertThat(nonExactIsCaseInsensitive).hasAttribute("id", id);

        final var exactIsCaseInsensitiveNegativeExample = page.locator("text='do not target me please'");
        assertThat(exactIsCaseInsensitiveNegativeExample).isHidden();

        final var exactIsCaseInsensitivePositiveExample = page.locator("text='Do not Target Me please'");
        assertThat(exactIsCaseInsensitivePositiveExample).hasAttribute("id", id);

        final var nonExactMatchesAsSubstring = page.locator("text=Lease");
        assertThat(nonExactMatchesAsSubstring).hasAttribute("id", id);

        final var exactMatchesWholeStringNegativeExample = page.locator("text='Lease'");
        assertThat(exactMatchesWholeStringNegativeExample).isHidden();

        final var exactMatchesWholeStringPositiveExample = page.locator("text='Do not Target Me please'");
        assertThat(exactMatchesWholeStringPositiveExample).hasAttribute("id", id);
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
