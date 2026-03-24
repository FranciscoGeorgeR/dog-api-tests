package com.dogapi.tests;

import com.dogapi.models.DogApiResponse;
import com.dogapi.utils.ApiConfig;
import com.dogapi.utils.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test suite for: GET /breed/{breed}/images
 *
 * Validates:
 *  - Correct images returned for known breeds
 *  - Image URLs follow expected CDN pattern
 *  - Sub-breed images endpoint
 *  - 404 + error status for invalid breeds
 */
@Epic("Dog API")
@Feature("Breed Images")
public class BreedImagesTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────────────────
    // Data Providers
    // ─────────────────────────────────────────────────────────────────────────

    @DataProvider(name = "validBreeds")
    public Object[][] validBreeds() {
        return new Object[][] {
            { ApiConfig.BREED_LABRADOR },
            { ApiConfig.BREED_HOUND },
            { ApiConfig.BREED_BULLDOG },
            { ApiConfig.BREED_RETRIEVER },
            { ApiConfig.BREED_POODLE }
        };
    }

    @DataProvider(name = "breedsWithSubBreeds")
    public Object[][] breedsWithSubBreeds() {
        return new Object[][] {
            { "hound",    "afghan"    },
            { "hound",    "blood"     },
            { "bulldog",  "english"   },
            { "retriever","golden"    },
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Happy-path tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test(dataProvider = "validBreeds",
          description = "GET /breed/{breed}/images should return HTTP 200 for known breeds")
    @Story("Breed Images")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Parameterised test: verifies HTTP 200 for multiple known breed slugs.")
    public void shouldReturn200ForKnownBreed(String breed) {
        given()
            .spec(requestSpec)
            .pathParam("breed", breed)
        .when()
            .get(ApiConfig.BREED_IMAGES)
        .then()
            .spec(successSpec);

        log.info(" HTTP 200 for breed: {}", breed);
    }

    @Test(description = "Response status field must be 'success' for labrador")
    @Story("Breed Images")
    @Severity(SeverityLevel.CRITICAL)
    public void statusFieldShouldBeSuccessForValidBreed() {
        given()
            .spec(requestSpec)
            .pathParam("breed", ApiConfig.BREED_LABRADOR)
        .when()
            .get(ApiConfig.BREED_IMAGES)
        .then()
            .spec(successSpec)
            .body("status", equalTo(ApiConfig.STATUS_SUCCESS));
    }

    @Test(description = "Image list for labrador must be non-empty")
    @Story("Breed Images")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Confirms that at least one image URL is returned for the 'labrador' breed.")
    public void imagListMustBeNonEmptyForLabrador() {
        DogApiResponse.BreedImages response =
            given()
                .spec(requestSpec)
                .pathParam("breed", ApiConfig.BREED_LABRADOR)
            .when()
                .get(ApiConfig.BREED_IMAGES)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedImages.class);

        assertNotNull(response.message, "message (image list) must not be null");
        assertFalse(response.message.isEmpty(), "image list must not be empty for labrador");
        log.info(" {} images found for labrador", response.message.size());
    }

    @Test(description = "All image URLs must match the expected CDN pattern")
    @Story("Breed Images")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Iterates every URL returned for 'hound' and asserts it matches the CDN regex.")
    public void imageUrlsMustMatchCdnPattern() {
        DogApiResponse.BreedImages response =
            given()
                .spec(requestSpec)
                .pathParam("breed", ApiConfig.BREED_HOUND)
            .when()
                .get(ApiConfig.BREED_IMAGES)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedImages.class);

        assertFalse(response.message.isEmpty(), "hound must have at least one image");

        for (String url : response.message) {
            assertThat("Image URL must match CDN pattern: " + url,
                url, matchesPattern(ApiConfig.IMAGE_URL_PATTERN));
        }
        log.info(" All {} hound image URLs match CDN pattern", response.message.size());
    }

    @Test(description = "Image URL path must contain the requested breed name")
    @Story("Breed Images")
    @Severity(SeverityLevel.NORMAL)
    @Description("Asserts that image URLs returned for a breed contain that breed's name in the path.")
    public void imageUrlsMustContainBreedName() {
        String breed = ApiConfig.BREED_LABRADOR;

        DogApiResponse.BreedImages response =
            given()
                .spec(requestSpec)
                .pathParam("breed", breed)
            .when()
                .get(ApiConfig.BREED_IMAGES)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedImages.class);

        for (String url : response.message) {
            assertThat("URL must contain breed name '" + breed + "': " + url,
                url.toLowerCase(), containsString(breed));
        }
    }

    @Test(description = "Image URLs must use HTTPS scheme")
    @Story("Breed Images")
    @Severity(SeverityLevel.NORMAL)
    @Description("Security check: all image URLs must be served over HTTPS.")
    public void imageUrlsMustUseHttps() {
        DogApiResponse.BreedImages response =
            given()
                .spec(requestSpec)
                .pathParam("breed", ApiConfig.BREED_POODLE)
            .when()
                .get(ApiConfig.BREED_IMAGES)
            .then()
                .spec(successSpec)
                .extract().as(DogApiResponse.BreedImages.class);

        for (String url : response.message) {
            assertTrue(url.startsWith("https://"),
                "Image URL must start with https://: " + url);
        }
    }

    @Test(dataProvider = "breedsWithSubBreeds",
          description = "Sub-breed images endpoint should return HTTP 200")
    @Story("Breed Images")
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that /breed/{breed}/{sub-breed}/images works for known sub-breeds.")
    public void subBreedImagesShouldReturn200(String breed, String subBreed) {
        String endpoint = "/breed/{breed}/{subBreed}/images";

        given()
            .spec(requestSpec)
            .pathParam("breed", breed)
            .pathParam("subBreed", subBreed)
        .when()
            .get(endpoint)
        .then()
            .spec(successSpec)
            .body("status", equalTo(ApiConfig.STATUS_SUCCESS));

        log.info(" Sub-breed images OK: {}/{}", breed, subBreed);
    }

    @Test(description = "Image list must be an array of strings")
    @Story("Breed Images")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates the data type of the 'message' field for breed images.")
    public void imageMessageMustBeAnArrayOfStrings() {
        given()
            .spec(requestSpec)
            .pathParam("breed", ApiConfig.BREED_RETRIEVER)
        .when()
            .get(ApiConfig.BREED_IMAGES)
        .then()
            .spec(successSpec)
            .body("message", instanceOf(List.class))
            .body("message[0]", instanceOf(String.class));
    }

    @Test(description = "Response time for breed images must be under 8 seconds")
    @Story("Breed Images")
    @Severity(SeverityLevel.MINOR)
    public void responseTimeShouldBeAcceptable() {
        Response response =
            given()
                .spec(requestSpec)
                .pathParam("breed", ApiConfig.BREED_LABRADOR)
            .when()
                .get(ApiConfig.BREED_IMAGES)
            .then()
                .extract().response();

        long responseTime = response.time();
        log.info("Breed images response time: {} ms", responseTime);
        assertThat("Response time must be under 8000ms", responseTime, lessThan(8000L));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Negative tests
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Invalid breed should return HTTP 404")
    @Story("Breed Images - Error Handling")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies the API returns a proper 404 when an unknown breed slug is requested.")
    public void invalidBreedShouldReturn404() {
        given()
            .spec(requestSpec)
            .pathParam("breed", ApiConfig.BREED_INVALID)
        .when()
            .get(ApiConfig.BREED_IMAGES)
        .then()
            .statusCode(ApiConfig.HTTP_NOT_FOUND);

        log.info(" HTTP 404 returned for invalid breed: {}", ApiConfig.BREED_INVALID);
    }

    @Test(description = "Invalid breed response status field should be 'error'")
    @Story("Breed Images - Error Handling")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures the API-level status flag reports 'error' for an unknown breed.")
    public void invalidBreedStatusFieldShouldBeError() {
        given()
            .spec(requestSpec)
            .pathParam("breed", ApiConfig.BREED_INVALID)
        .when()
            .get(ApiConfig.BREED_IMAGES)
        .then()
            .statusCode(ApiConfig.HTTP_NOT_FOUND)
            .body("status", equalTo(ApiConfig.STATUS_ERROR));
    }

    @Test(description = "Error response for invalid breed must contain a non-empty message")
    @Story("Breed Images - Error Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Confirms that the error response body includes a descriptive message string.")
    public void invalidBreedErrorResponseMustContainMessage() {
        given()
            .spec(requestSpec)
            .pathParam("breed", ApiConfig.BREED_INVALID)
        .when()
            .get(ApiConfig.BREED_IMAGES)
        .then()
            .statusCode(ApiConfig.HTTP_NOT_FOUND)
            .body("message", notNullValue())
            .body("message", not(emptyString()));
    }
}
