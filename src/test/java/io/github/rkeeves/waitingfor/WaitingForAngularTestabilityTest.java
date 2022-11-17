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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WaitingForAngularTestabilityTest {

    /**
     * What's the goal?
     * Demonstrate the usage of Angular Testability.
     *
     * What does it do?
     * We visit a book search site.
     * Click on different options "By Title" and "By Topic".
     * We will count how many results were on the screen for each case.
     * Then we'll compare them.
     * Clicking around causes the app to "load".
     * It takes a long time to fetch data from the backend, and run all the 3rd party codes etc.
     * So the time it takes for the app to "settle down" can differ greatly across different test runs.
     * In this example we use Angular Testability checks to determine "readyness".
     */
    @RepeatedTest(4)
    void example() {
        final var tab = page.locator(".stats-browse-pub-tab");
        page.navigate("https://ieeexplore.ieee.org/browse/books/title");

        tab.getByText("By Title").click();
        page.waitForFunction("window.getAllAngularTestabilities().filter(x => !x.isStable()).length === 0");
        final var countOne = page.locator("xpl-browse-results-item").count();

        tab.getByText("By Topic").click();
        page.waitForFunction("window.getAllAngularTestabilities().filter(x => !x.isStable()).length === 0");
        final var countTwo = page.locator("xpl-browse-results-item").count();

        assertEquals(countOne, countTwo);
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
