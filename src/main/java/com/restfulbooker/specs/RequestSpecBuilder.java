package com.restfulbooker.specs;

import com.restfulbooker.config.Configuration;
import com.restfulbooker.config.ConfigurationManager;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Central place to build RestAssured RequestSpecification with common configs.
 * Loads base URI, port, and base path from Configuration; sets JSON content type
 * and request/response logging. Reusable across all endpoints.
 */
public final class RequestSpecBuilder {

    private static final Configuration CONFIG = ConfigurationManager.getConfiguration();

    private RequestSpecBuilder() {
    }

    /**
     * Builds a RequestSpecification with shared API config, JSON content type,
     * and request/response logging filters.
     *
     * @return configured RequestSpecification
     */
    public static RequestSpecification build() {
        return new io.restassured.builder.RequestSpecBuilder()
                .setBaseUri(CONFIG.apiBaseUri())
                .setPort(CONFIG.apiPort())
                .setBasePath(CONFIG.apiBasePath())
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}
