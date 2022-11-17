package io.github.rkeeves.network;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class StubTheBackendTest {

    /**
     * What's the goal?
     * Demonstrate usage of API stubbing via Routes.
     *
     * What does it do?
     * We visit a book store app.
     * The client (app) tries to fetch the books from the backend.
     * In this simple example we intercept this "get all books" API call, and we instead provide the response.
     * We always return a "canned" answer to the client app, this is called stubbing.
     * Although this is a pretty dumb example, you get the point.
     * If you intercept GET, DELETE, POST etc. then you can fake away the whole backend basically.
     * For example: You get into a company, open the test codebase and see Order(1), Order(2) on the tests.
     * It is an insanely bad practice, because - most of the time - ordering is done, because the testers
     * don't have direct control over the state.
     * Imagine you have 60 idempotent tests, and one test which deletes everything.
     * In this case faking/mocking/stubbing away the non-idempotent action would mean that ordering of the tests is unnecessary.
     * Also, if you fake away the state then you can directly assert on it, instead of reading out state variables
     * from the volatile DOM of the client.
     */
    @Test
    void example() {
        final var expectedBook = Book.builder().build();
        final var stubbedResponse = new Gson().toJson(
                GetAllBooksApiResponse.builder()
                        .books(List.of(expectedBook))
                        .build()
        );
        page.route("**/BookStore/v1/Books", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("application/json")
                    .setBody(stubbedResponse));
        });
        page.navigate("https://demoqa.com/books");
        assertThat(page.locator(".action-buttons a")).hasCount(1);
        final var row = page.locator(".rt-tbody .rt-tr >> nth=0");
        assertThat(row.locator("a")).hasText(expectedBook.getTitle());
        assertThat(row.locator(".rt-td >> nth=2")).hasText(expectedBook.getAuthor());
        assertThat(row.locator(".rt-td >> nth=3")).hasText(expectedBook.getPublisher());
    }

    @Data
    @Builder
    static class GetAllBooksApiResponse {
        @SerializedName("books")
        List<Book> books;
    }

    @Data
    @Builder
    static class Book {
        @Builder.Default
        @SerializedName("isbn")
        String isbn = "9696969696969";
        @Builder.Default
        String title = "GitGud: Kernel Panic Boogalo";
        @Builder.Default
        @SerializedName("subTitle")
        String subTitle = "How the University of Minnesota has been banned from contributing to the Linux kernel";
        @Builder.Default
        @SerializedName("author")
        String author = "Prof. R. U. Serious";
        @Builder.Default
        @SerializedName("publish_date")
        String publishDate = "2020-06-04T08:48:39.000Z";
        @Builder.Default
        @SerializedName("publisher")
        String publisher = "This really happened";
        @Builder.Default
        @SerializedName("pages")
        Integer pages = 69;
        @Builder.Default
        @SerializedName("description")
        String description = "The frat boyz did it again!";
        @Builder.Default
        @SerializedName("website")
        String website = "https://youtu.be/81szj1vpEu8?t=158";
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
