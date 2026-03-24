package com.dogapi.tests;

import com.dogapi.models.DogApiResponse;
import com.dogapi.utils.ApiConfig;
import com.dogapi.utils.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test suite for: GET /breeds/image/random
 *
 * Validates:
 *  - Response structure and HTTP status
 *  - Image URL format and HTTPS
 *  - Randomness (multiple requests return different images)
 *  - Idempotency (endpoint is always reachable)
 */
@Epic("Dog API")
@Feature("Random Image")
public class RandomImageTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Happy-path tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "GET /breeds/image/random should return HTTP 200")
    @Story("Random Image")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Validates that the endpoint is reachable and returns HTTP 200 with JSON content type.")
    public void shouldReturn200() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_IMAGE_RANDOM)
        .then()
            .spec(successSpec);

        log.info(" GET /breeds/image/random returned HTTP 200");
    }

    @Test(description = "Response must contain 'message' and 'status' fields")
    @Story("Random Image")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Asserts that both mandatory top-level fields are present in the response JSON.")
    public void responseMustHaveMandatoryFields() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_IMAGE_RANDOM)
        .then()
            .spec(successSpec)
            .body("message", notNullValue())
            .body("status",  notNullValue());
    }

    @Test(description = "Status field must equal 'success'")
    @Story("Random Image")
    @Severity(SeverityLevel.CRITICAL)
    public void statusFieldShouldBeSuccess() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_IMAGE_RANDOM)
        .then()
            .spec(successSpec)
            .body("status", equalTo(ApiConfig.STATUS_SUCCESS));
    }

    @Test(description = "Message field must be a non-empty string")
    @Story("Random Image")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that 'message' is a non-blank String (the image URL).")
    public void messageMustBeNonEmptyString() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_IMAGE_RANDOM)
        .then()
            .spec(successSpec)
            .body("message", instanceOf(String.class))
            .body("message", not(emptyString()));
    }

    @Test(description = "Image URL must match the expected CDN pattern")
    @Story("Random Image")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the returned URL against the Dog CEO CDN regex pattern.")
    public void imageUrlMustMatchCdnPattern() {
        DogApiResponse.RandomImage response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_IMAGE_RANDOM)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.RandomImage.class);

        assertNotNull(response.message, "message (image URL) must not be null");
        assertThat("Image URL must match CDN pattern",
            response.message, matchesPattern(ApiConfig.IMAGE_URL_PATTERN));
        log.info(" Random image URL: {}", response.message);
    }

    @Test(description = "Image URL must use HTTPS scheme")
    @Story("Random Image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Security check: the image URL must be served over HTTPS.")
    public void imageUrlMustUseHttps() {
        DogApiResponse.RandomImage response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_IMAGE_RANDOM)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.RandomImage.class);

        assertTrue(response.message.startsWith("https://"),
            "Image URL must start with https://: " + response.message);
    }

    @Test(description = "Image URL must end with a valid image file extension")
    @Story("Random Image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Asserts that the image URL has a recognised extension: .jpg, .jpeg, .png, or .gif.")
    public void imageUrlMustHaveValidExtension() {
        DogApiResponse.RandomImage response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_IMAGE_RANDOM)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.RandomImage.class);

        String url = response.message.toLowerCase();
        boolean hasValidExtension = url.endsWith(".jpg")
                                 || url.endsWith(".jpeg")
                                 || url.endsWith(".png")
                                 || url.endsWith(".gif");
        assertTrue(hasValidExtension,
            "Image URL must end with .jpg/.jpeg/.png/.gif — got: " + url);
    }

    @Test(description = "Image URL must contain 'images.dog.ceo/breeds/' in the path")
    @Story("Random Image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Confirms that the returned URL points to the official Dog CEO CDN path.")
    public void imageUrlMustContainBreedsCdnPath() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_IMAGE_RANDOM)
        .then()
            .spec(successSpec)
            .body("message", containsString("images.dog.ceo/breeds/"));
    }

    @Test(description = "Repeated calls should return images from different breeds (randomness check)")
    @Story("Random Image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Makes 5 requests and asserts at least 2 distinct URLs are returned — a basic randomness sanity check.")
    public void repeatedCallsShouldReturnVariedImages() {
        int callCount = 5;
        Set<String> urls = new HashSet<>();

        for (int i = 0; i < callCount; i++) {
            DogApiResponse.RandomImage response =
                given()
                    .spec(requestSpec)
                .when()
                    .get(ApiConfig.BREEDS_IMAGE_RANDOM)
                .then()
                    .spec(successSpec)
                    .extract().as(DogApiResponse.RandomImage.class);

            urls.add(response.message);
        }

        log.info(" {} calls → {} distinct URLs: {}", callCount, urls.size(), urls);
        assertThat("At least 2 distinct random URLs expected from " + callCount + " calls",
            urls.size(), greaterThanOrEqualTo(2));
    }

    @Test(description = "Endpoint must be consistently reachable (idempotency check)")
    @Story("Random Image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Makes 3 consecutive requests and verifies all return HTTP 200 — basic availability check.")
    public void endpointMustBeConsistentlyReachable() {
        for (int i = 1; i <= 3; i++) {
            int statusCode =
                given()
                    .spec(requestSpec)
                .when()
                    .get(ApiConfig.BREEDS_IMAGE_RANDOM)
                .then()
                    .extract().statusCode();

            assertEquals(statusCode, ApiConfig.HTTP_OK,
                "Call #" + i + " should return HTTP 200 but got: " + statusCode);
        }
        log.info(" 3 consecutive calls all returned HTTP 200");
    }

    @Test(description = "Response time should be under 5 seconds")
    @Story("Random Image")
    @Severity(SeverityLevel.MINOR)
    public void responseTimeShouldBeAcceptable() {
        Response response =
            given()
                .spec(requestSpec)
            .when()
                .get(ApiConfig.BREEDS_IMAGE_RANDOM)
            .then()
                .extract().response();

        long responseTime = response.time();
        log.info("Random image response time: {} ms", responseTime);
        assertThat("Response time must be under 5000ms", responseTime, lessThan(5000L));
    }

    @Test(description = "Response Content-Type header must be application/json")
    @Story("Random Image")
    @Severity(SeverityLevel.NORMAL)
    @Description("Confirms the server sends the correct Content-Type header.")
    public void contentTypeMustBeJson() {
        given()
            .spec(requestSpec)
        .when()
            .get(ApiConfig.BREEDS_IMAGE_RANDOM)
        .then()
            .spec(successSpec)
            .contentType("application/json");
    }
}
