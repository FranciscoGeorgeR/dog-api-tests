package com.dogapi.utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected RequestSpecification  requestSpec;
    protected ResponseSpecification successSpec;

    @BeforeClass(alwaysRun = true)
    public void globalSetup() {
        log.info("=== Initialising Dog API Test Suite ===");
        log.info("Base URL: {}", ApiConfig.BASE_URL);

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestSpec = RequestSpecFactory.defaultSpec();
        successSpec = RequestSpecFactory.successSpec();

        log.info("REST Assured configuration applied.");
    }
}