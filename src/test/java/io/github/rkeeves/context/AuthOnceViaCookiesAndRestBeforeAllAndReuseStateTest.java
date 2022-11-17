package io.github.rkeeves.context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.RequestOptions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.util.List;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AuthOnceViaCookiesAndRestBeforeAllAndReuseStateTest {

    /**
     * What's the goal?
     * Demonstrate the usage of cookie manipulation and directly calling the backend API.
     *
     * What does it do?
     * "Before All" we do a quick manual login via a "technical context".
     * We will throw away this "technical context", but save its state into a static field (you can save it to a file etc.).
     * We will then reuse this state in all "Before Each" to create already logged in contexts.
     * We logout in the tests just to prove that each test is initialized into the same "logged in" state,
     * so if you log out in one test, it has no effect on other tests.
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

    static List<Cookie> cookies;

    BrowserContext context;

    Page page;

    @BeforeAll
    static void beforeAll() {
        playwright = Playwright.create();
        final var requests = playwright.request()
                .newContext(new APIRequest.NewContextOptions().setBaseURL("https://demoqa.com"));
        final var generateTokenRespone = requests.post(
                "/Account/v1/GenerateToken",
                RequestOptions.create()
                        .setData(
                                Map.ofEntries(
                                        Map.entry("userName", UNAME),
                                        Map.entry("password", PASS)
                                )
                        ));
        Assumptions.assumeTrue(generateTokenRespone.ok());
        final var loginResponse = requests.post(
                "/Account/v1/Login",
                RequestOptions.create()
                        .setData(
                                Map.ofEntries(
                                        Map.entry("userName", UNAME),
                                        Map.entry("password", PASS)
                                )
                        ));
        Assumptions.assumeTrue(loginResponse.ok());
        final var dto = new Gson().fromJson(loginResponse.text(), LoginResponseDTO.class);
        cookies = DemoQaCookies.cookiesFor(dto);
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @UtilityClass
    static class DemoQaCookies {

        public static List<Cookie> cookiesFor(LoginResponseDTO loginResponseDTO) {
            return List.of(
                    cookieOf("userID", loginResponseDTO.getUserId()),
                    cookieOf("userName", loginResponseDTO.getUsername()),
                    cookieOf("token", loginResponseDTO.getToken()),
                    cookieOf("expires", loginResponseDTO.getExpires())
            );
        }

        private static Cookie cookieOf(String key, String val) {
            return new Cookie(key, val).setPath("/").setDomain("demoqa.com");
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class LoginResponseDTO {
        @SerializedName("userId")
        String userId;
        @SerializedName("username")
        String username;
        @SerializedName("password")
        String password;
        @SerializedName("token")
        String token;
        @SerializedName("expires")
        String expires;
        @SerializedName("created_date")
        String createdDate;
        @SerializedName("isActive")
        Boolean isActive;
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        context.addCookies(cookies);
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }
}
