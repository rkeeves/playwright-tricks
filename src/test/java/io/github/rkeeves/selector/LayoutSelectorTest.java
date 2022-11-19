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

public class LayoutSelectorTest {


    /**
     * What's the goal?
     * Demonstrate the usage of layout selectors.
     *
     * What does it do?
     * ...
     */
    @Test
    void example() {
        final var fullName = "Adam";
        final var email = "adam@adam.com";
        final var currentAddress = "Adam current";
        final var permanentAddress ="Adam permanent";

        page.navigate("https://demoqa.com/text-box");

        final var inputFullName = page.locator("input:right-of(:text('Full Name'))").first();
        final var inputEmail = page.locator("input:right-of(:text('Email'))").first();
        final var inputCurrentAddress = page.locator("textarea:right-of(:text('Current Address'))").first();
        final var inputPermanentAddress = page.locator("textarea:right-of(:text('Permanent Address'))").first();
        inputFullName.fill(fullName);
        inputEmail.fill(email);
        inputCurrentAddress.fill(currentAddress);
        inputPermanentAddress.fill(permanentAddress);

        page.getByText("Submit").click();

        final var output = page.locator("#output");
        final var outputFullName = output.locator("#name");
        final var outputEmail = output.locator("#email");
        final var outputCurrentAddress = output.locator("#currentAddress");
        final var outputPermanentAddress = output.locator("#permanentAddress");
        assertThat(outputFullName).containsText(fullName);
        assertThat(outputEmail).containsText(email);
        assertThat(outputCurrentAddress).containsText(currentAddress);
        assertThat(outputPermanentAddress).containsText(permanentAddress);
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
