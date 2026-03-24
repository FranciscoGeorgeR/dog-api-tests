package com.dogapi.utils;

import com.dogapi.utils.ApiConfig;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class RequestSpecFactory {

    private RequestSpecFactory() {}

    public static RequestSpecification defaultSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .setConfig(io.restassured.RestAssured.config()
                        .connectionConfig(io.restassured.config.ConnectionConfig.connectionConfig()
                                .closeIdleConnectionsAfterEachResponse())
                        .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", ApiConfig.CONNECTION_TIMEOUT)
                                .setParam("http.socket.timeout", ApiConfig.READ_TIMEOUT)))
                .addFilter(new AllureRestAssured())
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification successSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(ApiConfig.HTTP_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
