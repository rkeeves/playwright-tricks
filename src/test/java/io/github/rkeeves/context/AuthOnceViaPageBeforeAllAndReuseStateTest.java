package io.github.rkeeves.context;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AuthOnceViaPageBeforeAllAndReuseStateTest {

    /**
     * What's the goal?
     * Demonstrate the usage of storageState.
     *
     * What does it do?
     * "Before All" we do a quick manual login via a "technical context".
     * We will throw away this "technical context", but save its state into a static field (you can save it to a file etc.).
     * We will then reuse this state in all "Before Each" to create already logged in contexts.
     * We log out in the tests to prove that the contexts are isolated.
     * Aka: mutating one context - by logging out - has no effect on other contexts.
     */
    @RepeatedTest(2)
    void example() {
        page.navigate("https://demoqa.com/books");
        assertThat(page.locator("#userName-value")).hasText(UNAME);
        page.getByText("Log out").click();
        assertThat(page).hasURL("https://demoqa.com/login");
    }

    static final String UNAME = "someuser";

    static final String PASS = "someuser1A@";

    static Playwright playwright;

    static Browser browser;

    static String storageState;

    BrowserContext context;

    Page page;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        try(final var technicalContext = browser.newContext()) {
            final var technicalPage = technicalContext.newPage();
            technicalPage.navigate("https://demoqa.com/login");
            technicalPage.locator("#userName").fill(UNAME);
            technicalPage.locator("#password").fill(PASS);
            technicalPage.locator("#login").scrollIntoViewIfNeeded();
            technicalPage.locator("#login").click();
            assertThat(technicalPage).hasURL("https://demoqa.com/profile");
            storageState = technicalContext.storageState();
        }
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        Assumptions.assumeFalse(storageState == null);
        Assumptions.assumeFalse(storageState.isBlank());
        context = browser.newContext(
                new Browser.NewContextOptions().setStorageState(storageState)
        );
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }
}
