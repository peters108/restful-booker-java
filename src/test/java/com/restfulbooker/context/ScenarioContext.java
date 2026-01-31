package com.restfulbooker.context;

import io.restassured.response.Response;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Shares data between Cucumber steps within a scenario (stateful context).
 * Uses ConcurrentHashMap for thread-safe access when scenarios run in parallel.
 */
public class ScenarioContext {

    /** Key for the last API response (used by common steps). */
    private static final String RESPONSE_KEY = "RESPONSE";

    /** Request body for the next "I send … request" step (e.g. auth payload, booking payload). */
    public static final String REQUEST_BODY = "REQUEST_BODY";

    private final ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();

    public void setContext(String key, Object value) {
        context.put(key, value);
    }

    public Object getContext(String key) {
        return context.get(key);
    }

    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /**
     * Stores the last response from any API call. Use this so "the response HTTP code is …"
     * and other common steps work for any endpoint.
     */
    public void setResponse(Response response) {
        context.put(RESPONSE_KEY, response);
    }

    /**
     * Returns the last response stored via {@link #setResponse(Response)}, or null if none.
     */
    public Response getResponse() {
        Object value = context.get(RESPONSE_KEY);
        return value instanceof Response ? (Response) value : null;
    }

    /**
     * Clears all stored data. Call from a @Before hook to reset context between scenarios.
     */
    public void clear() {
        context.clear();
    }
}
