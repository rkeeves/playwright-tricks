package io.github.rkeeves.waitingfor;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class WaitingForRequestToFinishTest {

    /**
     * What's the goal?
     * Demonstrate the usage of Request/Response inspection.
     *
     * What does it do?
     * We will select a country.
     * Selecting the country triggers xhr (AJAX, JSF etc.).
     * The client will update the selectable cities based on the country.
     * We must wait for the ajax to end.
     * This is a contrived example...also just waiting for the POST to finish might not be sufficient if:
     * - the client does other long tasks (like heavy DOM manipulation)
     * - the client - when the POST finishes - starts another request
     */
    @RepeatedTest(4)
    void example() {
        final var countrySelect = page.locator("div[id$=':country']");
        final var countryPanel = page.locator("div[id$=':country_panel']");
        final var citySelect = page.locator("div[id$=':city']");
        final var cityPanel = page.locator("div[id$=':city_panel']");

        page.navigate("https://www.primefaces.org/showcase-v8/ui/ajax/dropdown.xhtml");

        countrySelect.locator("label").click();
        assertThat(countryPanel).isVisible();
        final String country = "Brazil";
        page.waitForRequestFinished(
                new Page.WaitForRequestFinishedOptions()
                        .setPredicate(req ->
                                req.url().endsWith("/dropdown.xhtml")
                                && req.method().equals("POST")),
                () -> countryPanel.locator("li[data-label='" + country + "']").click());
        assertThat(countryPanel).isHidden();
        assertThat(countrySelect.locator("label")).hasText(country);

        citySelect.locator("label").click();
        assertThat(cityPanel).isVisible();
        final String city = "Salvador";
        cityPanel.locator("li[data-label='" + city + "']").click();
        assertThat(cityPanel).isHidden();
        assertThat(citySelect.locator("label")).hasText(city);
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
