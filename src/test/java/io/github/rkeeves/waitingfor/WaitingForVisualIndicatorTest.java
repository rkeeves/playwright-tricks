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

public class WaitingForVisualIndicatorTest {

    /**
     * What's the goal?
     * Demonstrate the usage of assertThat's polling behavior.
     *
     * What does it do?
     * We will select a country.
     * Selecting the country triggers xhr (AJAX, JSF etc.).
     * The client will update the selectable cities based on the country.
     * A small loading icon will appear.
     * We'll wait for this icon to disappear.
     * This technique has many problems on its own, but it is the most basic of all possible strategies.
     */
    @RepeatedTest(4)
    void example() {
        final var countrySelect = page.locator("div[id$=':country']");
        final var countryPanel = page.locator("div[id$=':country_panel']");
        final var citySelect = page.locator("div[id$=':city']");
        final var cityPanel = page.locator("div[id$=':city_panel']");
        final var statusIndicator = page.locator(".status-indicator");

        page.navigate("https://www.primefaces.org/showcase/ui/ajax/dropdown.xhtml");

        countrySelect.locator("label").click();
        assertThat(countryPanel).isVisible();
        final String country = "Brazil";
        countryPanel.locator("li[data-label='" + country + "']").click();
        assertThat(countryPanel).isHidden();
        assertThat(countrySelect.locator("label")).hasText(country);

        assertThat(statusIndicator.locator(".pi-spinner")).isHidden();

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
