package io.github.rkeeves.context;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import lombok.Value;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleContextsInOneScenarioTest {

    /**
     * What's the goal?
     * Demonstrate that multiple contexts do NOT share state.
     *
     * What does it do?
     * Two different contexts log in as different users.
     * At the end we assert that they have different cookies.
     * (Aka logging in with beta does not mutate alpha's state).
     */
    @Test
    void example() {
        final var expectedUserAlpha = "standard_user";
        final var actualUserAlpha = AppSteps.using(alphaPage)
                .visit()
                .fillUname(expectedUserAlpha)
                .fillPass("secret_sauce")
                .submit()
                .shouldBeInInventory()
                .acquireCurrentUser()
                .orElse("cookie did not exist");

        final var expectedUserBeta = "problem_user";
        final var actualUserBeta = AppSteps.using(betaPage)
                .visit()
                .fillUname(expectedUserBeta)
                .fillPass("secret_sauce")
                .submit()
                .shouldBeInInventory()
                .acquireCurrentUser()
                .orElse("cookie did not exist");

        assertEquals(expectedUserAlpha, actualUserAlpha);
        assertEquals(expectedUserBeta, actualUserBeta);
    }


    static Playwright playwright;

    static Browser browser;

    BrowserContext alphaContext;

    Page alphaPage;

    BrowserContext betaContext;

    Page betaPage;

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
        alphaContext = browser.newContext();
        alphaPage = alphaContext.newPage();
        betaContext = browser.newContext();
        betaPage = betaContext.newPage();
    }

    @AfterEach
    void closeContext() {
        alphaContext.close();
        betaContext.close();
    }

    @Value(staticConstructor = "using")
    static class AppSteps {

        Page page;

        public AppSteps visit() {
            page.navigate("https://www.saucedemo.com/");
            return this;
        }

        public AppSteps fillUname(String uname) {
            page.locator("*[data-test='username']").fill(uname);
            return this;
        }

        public AppSteps fillPass(String pass) {
            page.locator("*[data-test='password']").fill(pass);
            return this;
        }

        public AppSteps submit() {
            page.locator("*[data-test='login-button']").click();
            return this;
        }

        public AppSteps shouldBeInInventory() {
            assertThat(page).hasURL("https://www.saucedemo.com/inventory.html");
            return this;
        }

        public Optional<String> acquireCurrentUser() {
            return page.context()
                    .cookies()
                    .stream()
                    .filter(cookie -> "session-username".equals(cookie.name))
                    .map(cookie -> cookie.value)
                    .findFirst();
        }
    }
}
