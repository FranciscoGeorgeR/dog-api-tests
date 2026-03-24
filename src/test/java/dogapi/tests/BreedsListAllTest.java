package com.dogapi.tests;

import com.dogapi.models.DogApiResponse;
import com.dogapi.utils.ApiConfig;
import com.dogapi.utils.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test suite for: GET /breeds/list/all
 *
 * Validates:
 *  - HTTP status and response structure
 *  - Presence and types of well-known breeds
 *  - Sub-breed lists (e.g. hound → [afghan, blood])
 *  - Edge cases (empty breed key means no sub-breeds)
 */
@Epic("Dog API")
@Feature("Breeds List")
public class BreedsListAllTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Happy-path tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "GET /breeds/list/all should return HTTP 200")
    @Story("List All Breeds")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that the endpoint is reachable and returns HTTP 200 with JSON content type.")
    public void shouldReturn200WithJsonContentType() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_LIST_ALL)
        .then()
            .spec(successSpec);

        log.info(" GET /breeds/list/all returned HTTP 200");
    }

    @Test(description = "Response body must contain 'message' and 'status' fields")
    @Story("List All Breeds")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Asserts that the response JSON has the mandatory top-level fields.")
    public void shouldHaveMandatoryResponseFields() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_LIST_ALL)
        .then()
            .spec(successSpec)
            .body("message", notNullValue())
            .body("status",  notNullValue());
    }

    @Test(description = "Status field must equal 'success'")
    @Story("List All Breeds")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the API-level status flag reports success.")
    public void statusFieldShouldBeSuccess() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_LIST_ALL)
        .then()
            .spec(successSpec)
            .body("status", equalTo(ApiConfig.STATUS_SUCCESS));
    }

    @Test(description = "Message should contain a non-empty breeds map")
    @Story("List All Breeds")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Asserts that 'message' is a map with at least one breed entry.")
    public void messageShouldBeNonEmptyMap() {
        DogApiResponse.BreedsListAll response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_LIST_ALL)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedsListAll.class);

        assertNotNull(response.message, "message must not be null");
        assertFalse(response.message.isEmpty(), "message map must contain at least one breed");
        log.info(" Total breeds found: {}", response.message.size());
    }

    @Test(description = "Known breed 'labrador' must be present in the list")
    @Story("List All Breeds")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Checks that the well-known breed 'labrador' is included in the response.")
    public void shouldContainLabradorBreed() {
        assertBreedIsPresent(ApiConfig.BREED_LABRADOR);
    }

    @Test(description = "Known breed 'hound' must be present in the list")
    @Story("List All Breeds")
    @Severity(SeverityLevel.NORMAL)
    @Description("Checks that the well-known breed 'hound' is included in the response.")
    public void shouldContainHoundBreed() {
        assertBreedIsPresent(ApiConfig.BREED_HOUND);
    }

    @Test(description = "Known breed 'bulldog' must be present in the list")
    @Story("List All Breeds")
    @Severity(SeverityLevel.NORMAL)
    public void shouldContainBulldogBreed() {
        assertBreedIsPresent(ApiConfig.BREED_BULLDOG);
    }

    @Test(description = "Known breed 'poodle' must be present in the list")
    @Story("List All Breeds")
    @Severity(SeverityLevel.NORMAL)
    public void shouldContainPoodleBreed() {
        assertBreedIsPresent(ApiConfig.BREED_POODLE);
    }

    @Test(description = "Sub-breeds list values must always be arrays (never null)")
    @Story("List All Breeds")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Iterates through every breed entry and confirms its sub-breed value is a List, not null.")
    public void subBreedValuesMustAlwaysBeArrays() {
        DogApiResponse.BreedsListAll response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_LIST_ALL)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedsListAll.class);

        for (Map.Entry<String, List<String>> entry : response.message.entrySet()) {
            assertNotNull(entry.getValue(),
                "Sub-breed list for '" + entry.getKey() + "' must not be null");
            assertThat("Sub-breed value must be a List for breed: " + entry.getKey(),
                entry.getValue(), instanceOf(List.class));
        }
        log.info(" All {} breeds have valid sub-breed arrays", response.message.size());
    }

    @Test(description = "'hound' sub-breeds should include known sub-types")
    @Story("List All Breeds")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that the 'hound' breed lists well-known sub-breeds like 'afghan' and 'blood'.")
    public void houndShouldHaveKnownSubBreeds() {
        DogApiResponse.BreedsListAll response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_LIST_ALL)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedsListAll.class);

        assertTrue(response.message.containsKey("hound"), "hound breed must exist");
        List<String> houndSubs = response.message.get("hound");
        assertThat(houndSubs, hasItem("afghan"));
        assertThat(houndSubs, hasItem("blood"));
        log.info(" hound sub-breeds: {}", houndSubs);
    }

    @Test(description = "Breed names must be lowercase strings with no whitespace")
    @Story("List All Breeds")
    @Severity(SeverityLevel.NORMAL)
    @Description("Asserts that all breed keys follow the lowercase-only naming convention.")
    public void breedNamesMustBeLowercase() {
        DogApiResponse.BreedsListAll response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_LIST_ALL)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedsListAll.class);

        for (String breed : response.message.keySet()) {
            assertThat("Breed key must be lowercase: " + breed,
                breed, matchesPattern("[a-z]+"));
        }
    }

    @Test(description = "Response time should be under 5 seconds")
    @Story("List All Breeds")
    @Severity(SeverityLevel.MINOR)
    @Description("Non-functional: verifies the endpoint responds within an acceptable time threshold.")
    public void responseTimeShouldBeAcceptable() {
        Response response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_LIST_ALL)
            .then()
                .extract().response();

        long responseTime = response.time();
        log.info("Response time: {} ms", responseTime);
        assertThat("Response time must be under 5000ms", responseTime, lessThan(5000L));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    private void assertBreedIsPresent(String breed) {
        DogApiResponse.BreedsListAll response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_LIST_ALL)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedsListAll.class);

        assertTrue(response.message.containsKey(breed),
            "Breed '" + breed + "' must be present in /breeds/list/all");
        log.info(" Breed '{}' found in list", breed);
    }
}
