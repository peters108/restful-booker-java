package com.restfulbooker.client;

import com.restfulbooker.specs.RequestSpecBuilder;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Generic REST client for sending requests by method and path.
 * Used by step definitions that specify "METHOD /path" in one string.
 */
public final class RestClient {

    /**
     * Sends a request with the given method, path, and optional body.
     *
     * @param method HTTP method (GET, POST, PUT, DELETE, PATCH, etc.)
     * @param path   path (e.g. "/auth"). Base URI/path from config is applied by the spec.
     * @param body   request body, or null for no body
     * @return raw response for assertions
     */
    public Response send(String method, String path, Object body) {
        return send(method, path, body, null);
    }

    /**
     * Sends a request with the given method, path, optional body, and headers.
     *
     * @param method  HTTP method (GET, POST, PUT, DELETE, PATCH, etc.)
     * @param path    path (e.g. "/auth"). Base URI/path from config is applied by the spec.
     * @param body    request body, or null for no body
     * @param headers request headers, or null for none
     * @return raw response for assertions
     */
    public Response send(String method, String path, Object body, Map<String, String> headers) {
        RequestSpecification spec = given().spec(RequestSpecBuilder.build());
        if (headers != null) {
            spec = spec.headers(headers);
        }
        if (body != null) {
            spec = spec.body(body);
        }
        return spec.when().request(Method.valueOf(method.toUpperCase()), path).then().extract().response();
    }
}
