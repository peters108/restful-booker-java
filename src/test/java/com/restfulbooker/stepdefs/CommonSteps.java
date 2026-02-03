package com.restfulbooker.stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.restfulbooker.client.RestClient;
import com.restfulbooker.context.ScenarioContext;
import com.restfulbooker.hooks.TestHooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Generic step definitions reusable across all endpoints.
 */
public class CommonSteps {

    private static final String PAYLOADS_PREFIX = "payloads/";
    private static final String API_ENDPOINT_KEY = "API_ENDPOINT";

    private static final Set<String> VALID_HTTP_METHODS = Set.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestClient restClient = new RestClient();

    // ========== Request Steps (METHOD and path in one string) ==========

    @When("I send {string} request")
    public void iSendRequest(String methodAndPath) {
        Object body = getContext().getContext(ScenarioContext.REQUEST_BODY);
        sendRequest(methodAndPath, body);
    }

    @When("I send {string} request with the following payload:")
    public void iSendRequestWithPayload(String methodAndPath, String payload) {
        sendRequest(methodAndPath, payload);
    }

    @When("I send {string} request with payload from file {string}")
    public void iSendRequestWithPayloadFromFile(String methodAndPath, String filePath) {
        String payload = readClasspathResource(PAYLOADS_PREFIX + filePath);
        sendRequest(methodAndPath, payload);
    }

    // ========== Endpoint and Payload Setup Steps ==========

    @Given("I have API endpoint: {string}")
    public void iHaveApiEndpoint(String methodAndPath) {
        getContext().setContext(API_ENDPOINT_KEY, methodAndPath);
    }

    @Given("I have request payload from file: {string}")
    public void iHaveRequestPayloadFromFile(String filePath) {
        String payload = readClasspathResource(PAYLOADS_PREFIX + filePath);
        getContext().setContext(ScenarioContext.REQUEST_BODY, payload);
    }

    @Given("I set JSONpath {string} in payload to {value}")
    public void iSetJsonPathInPayloadTo(String jsonPath, String value) {
        String payload = getPayloadFromContext();
        String modifiedPayload = modifyJsonPath(payload, jsonPath, resolveVariables(value));
        getContext().setContext(ScenarioContext.REQUEST_BODY, modifiedPayload);
    }

    @Given("I remove JSONpath {string} from payload")
    public void iRemoveJsonPathFromPayload(String jsonPath) {
        String payload = getPayloadFromContext();
        String modifiedPayload = removeJsonPath(payload, jsonPath);
        getContext().setContext(ScenarioContext.REQUEST_BODY, modifiedPayload);
    }

    @When("I call the API")
    public void iCallTheApi() {
        Object endpoint = getContext().getContext(API_ENDPOINT_KEY);
        Assert.assertNotNull(endpoint, "API endpoint not set. Use 'Given I have API endpoint: ...' first.");
        String methodAndPath = String.valueOf(endpoint);
        Object body = getContext().getContext(ScenarioContext.REQUEST_BODY);
        sendRequest(methodAndPath, body);
    }

    private void sendRequest(String methodAndPath, Object body) {
        MethodAndPath parsed = parseMethodAndPath(resolveVariablesInPath(methodAndPath));
        Response response = restClient.send(parsed.method(), parsed.path(), body);
        getContext().setResponse(response);
    }

    // ========== HTTP Response Assertions ==========

    @Then("the response HTTP code is {int}")
    public void theResponseHttpCodeIs(int expectedStatusCode) {
        Response response = getResponse();
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode);
    }

    @Then("the response body matches the schema {string}")
    public void theResponseBodyMatchesTheSchema(String schemaPath) {
        Response response = getResponse();
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }

    // ========== JSONPath Existence ==========

    @Then("JSONpath {string} has value")
    public void jsonPathHasValue(String jsonPath) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNotNull(value, "JSONpath '" + jsonPath + "' has no value");
    }

    @Then("JSONpath {string} has no value")
    public void jsonPathHasNoValue(String jsonPath) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNull(value, "JSONpath '" + jsonPath + "' has value but was expected to have no value");
    }

    // ========== JSONPath Emptiness ==========

    @Then("JSONpath {string} is not empty")
    public void jsonPathIsNotEmpty(String jsonPath) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNotNull(value, "JSONpath '" + jsonPath + "' has no value");

        if (value instanceof String) {
            Assert.assertFalse(((String) value).isBlank(),
                    "JSONpath '" + jsonPath + "' is empty string");
        } else if (value instanceof List) {
            Assert.assertFalse(((List<?>) value).isEmpty(),
                    "JSONpath '" + jsonPath + "' is empty array");
        }
    }

    @Then("JSONpath {string} is empty")
    public void jsonPathIsEmpty(String jsonPath) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNotNull(value,
                "JSONpath '" + jsonPath + "' has no value. Use 'JSONpath has no value' instead");

        if (value instanceof String) {
            Assert.assertTrue(((String) value).isBlank(),
                    "JSONpath '" + jsonPath + "' is not empty: '" + value + "'");
        } else if (value instanceof List) {
            Assert.assertTrue(((List<?>) value).isEmpty(),
                    "JSONpath '" + jsonPath + "' array is not empty, has " + ((List<?>) value).size() + " items");
        } else {
            Assert.fail("JSONpath '" + jsonPath + "' cannot be empty (type: "
                    + value.getClass().getSimpleName() + "). Only strings and arrays can be empty");
        }
    }

    @Then("JSONpath {string} is empty array")
    public void jsonPathIsEmptyArray(String jsonPath) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNotNull(value, "JSONpath '" + jsonPath + "' has no value");
        Assert.assertTrue(value instanceof List,
                "JSONpath '" + jsonPath + "' is not an array, got: " + value.getClass().getSimpleName());
        Assert.assertTrue(((List<?>) value).isEmpty(),
                "JSONpath '" + jsonPath + "' array is not empty");
    }

    // ========== JSONPath Value Comparison ==========

    @Then("JSONpath {string} value is {value}")
    public void jsonPathValueIs(String jsonPath, String expectedValue) {
        Object actualValue = getJsonPathValue(jsonPath);
        Assert.assertNotNull(actualValue, "Response body has no value at JSONpath '" + jsonPath + "'");

        expectedValue = resolveVariables(expectedValue);
        Object convertedExpected = convertToActualType(actualValue, expectedValue);

        Assert.assertEquals(actualValue, convertedExpected,
                "JSONpath '" + jsonPath + "' value did not match");
    }

    @Then("JSONpath {string} value is {ofType}")
    public void jsonPathValueIsOfType(String jsonPath, String ofType) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNotNull(value, "Response body has no value at JSONpath '" + jsonPath + "'");

        switch (ofType) {
            case "string":
                Assert.assertTrue(value instanceof String,
                        "JSONpath '" + jsonPath + "' value is not a string, got: " + value.getClass().getSimpleName());
                break;
            case "number":
                Assert.assertTrue(value instanceof Number,
                        "JSONpath '" + jsonPath + "' value is not a number, got: " + value.getClass().getSimpleName());
                break;
            case "boolean":
                Assert.assertTrue(value instanceof Boolean,
                        "JSONpath '" + jsonPath + "' value is not a boolean, got: " + value.getClass().getSimpleName());
                break;
            default:
                Assert.fail("Unknown type keyword '" + ofType + "'. Use: string, number, or boolean.");
        }
    }

    // ========== Variable Storage ==========

    @Then("I save JSONpath {string} value as {string}")
    public void saveJsonPathValueAs(String jsonPath, String variableName) {
        Object value = getJsonPathValue(jsonPath);
        Assert.assertNotNull(value, "JSONpath '" + jsonPath + "' has no value");
        getContext().setContext(variableName, String.valueOf(value));
    }

    @Then("I save value {value} as {string}")
    public void saveValueAs(String value, String variableName) {
        String resolvedValue = resolveVariables(value);
        getContext().setContext(variableName, resolvedValue);
    }

    // ========== Private Helper Methods ==========

    private static record MethodAndPath(String method, String path) {}

    /**
     * Parses "METHOD /path" string. Requires both method and path (space-separated).
     * Validates HTTP method and that path starts with /.
     */
    private MethodAndPath parseMethodAndPath(String methodAndPath) {
        Assert.assertNotNull(methodAndPath, "Method and path string must not be null");
        int firstSpace = methodAndPath.indexOf(' ');
        Assert.assertTrue(firstSpace > 0,
                "Method and path must be specified as 'METHOD /path', e.g. 'POST /auth'. Got: " + methodAndPath);
        String method = methodAndPath.substring(0, firstSpace).trim().toUpperCase();
        String path = methodAndPath.substring(firstSpace + 1).trim();
        Assert.assertFalse(path.isEmpty(), "Path must not be empty. Got: " + methodAndPath);
        Assert.assertTrue(VALID_HTTP_METHODS.contains(method),
                "Invalid HTTP method: '" + method + "'. Expected one of: " + String.join(", ", VALID_HTTP_METHODS));
        Assert.assertTrue(path.startsWith("/"),
                "Path must start with '/'. Got: '" + path + "'");
        return new MethodAndPath(method, path);
    }

    private String readClasspathResource(String resourcePath) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream in = loader.getResourceAsStream(resourcePath)) {
            Assert.assertNotNull(in, "Resource not found on classpath: " + resourcePath);
            try (Scanner s = new Scanner(in, StandardCharsets.UTF_8).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        } catch (Exception e) {
            Assert.fail("Failed to read classpath resource '" + resourcePath + "': " + e.getMessage());
            throw new AssertionError("unreachable");
        }
    }

    private Response getResponse() {
        Response response = getContext().getResponse();
        Assert.assertNotNull(response, "No response found; ensure a request step ran first.");
        return response;
    }

    private Object getJsonPathValue(String jsonPath) {
        return getResponse().jsonPath().get(jsonPath);
    }

    private String resolveVariables(String input) {
        if (!input.startsWith("$")) {
            return input;
        }

        switch (input) {
            case "$UUID":
                return UUID.randomUUID().toString();
            case "$UUID_NODASH":
                return UUID.randomUUID().toString().replace("-", "");
            case "$TIMESTAMP":
                return String.valueOf(System.currentTimeMillis());
            default:
                Object value = getContext().getContext(input);
                Assert.assertNotNull(value, "Variable '" + input + "' not found in context");
                return String.valueOf(value);
        }
    }

    private Object convertToActualType(Object actualValue, String expectedValue) {
        if ("null".equalsIgnoreCase(expectedValue)) {
            return null;
        }

        if (actualValue instanceof Number && isNumeric(expectedValue)) {
            return expectedValue.contains(".")
                ? Double.parseDouble(expectedValue)
                : Integer.parseInt(expectedValue);
        }

        if (actualValue instanceof Boolean) {
            if ("true".equalsIgnoreCase(expectedValue) || "false".equalsIgnoreCase(expectedValue)) {
                return Boolean.parseBoolean(expectedValue);
            }
            Assert.fail("Actual value is boolean (" + actualValue +
                    ") but expected value (" + expectedValue + ") is not");
        }

        return expectedValue;
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private ScenarioContext getContext() {
        return TestHooks.getScenarioContext();
    }

    // ========== JSON Payload Manipulation Helpers ==========

    private String getPayloadFromContext() {
        Object payload = getContext().getContext(ScenarioContext.REQUEST_BODY);
        Assert.assertNotNull(payload, "Request payload not set. Use 'Given I have request payload from file: ...' first.");
        return String.valueOf(payload);
    }

    /**
     * Modifies a JSON payload by setting a value at the given JSONPath.
     * Supports simple paths (e.g., "firstname") and nested paths (e.g., "bookingdates.checkin").
     */
    private String modifyJsonPath(String jsonPayload, String jsonPath, String value) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(jsonPayload);
            String[] pathParts = jsonPath.split("\\.");
            
            // Navigate to parent node
            JsonNode parent = root;
            for (int i = 0; i < pathParts.length - 1; i++) {
                String part = pathParts[i];
                if (parent == null) {
                    Assert.fail("JSONPath '" + jsonPath + "' parent node is null at path segment '" + part + "'");
                }
                if (!parent.has(part)) {
                    // Create missing intermediate objects
                    if (!(parent instanceof ObjectNode)) {
                        Assert.fail("JSONPath '" + jsonPath + "' cannot modify non-object node at path segment '" + part + "'");
                    }
                    ((ObjectNode) parent).set(part, OBJECT_MAPPER.createObjectNode());
                }
                parent = parent.get(part);
                if (parent == null) {
                    Assert.fail("JSONPath '" + jsonPath + "' path segment '" + part + "' is null");
                }
            }
            
            // Set value at final path
            String finalKey = pathParts[pathParts.length - 1];
            if (parent == null) {
                Assert.fail("JSONPath '" + jsonPath + "' parent node is null");
            }
            if (!(parent instanceof ObjectNode)) {
                Assert.fail("JSONPath '" + jsonPath + "' cannot modify non-object node. Parent at path is not an object");
            }
            ObjectNode parentObject = (ObjectNode) parent;
            
            // Try to infer type from value
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                parentObject.put(finalKey, Boolean.parseBoolean(value));
            } else if (isNumeric(value)) {
                if (value.contains(".")) {
                    parentObject.put(finalKey, Double.parseDouble(value));
                } else {
                    parentObject.put(finalKey, Integer.parseInt(value));
                }
            } else {
                parentObject.put(finalKey, value);
            }
            
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            Assert.fail("Failed to modify JSONPath '" + jsonPath + "' in payload: " + e.getMessage());
            throw new AssertionError("unreachable");
        }
    }

    /**
     * Removes a field from a JSON payload at the given JSONPath.
     * Supports simple paths (e.g., "firstname") and nested paths (e.g., "bookingdates.checkin").
     */
    private String removeJsonPath(String jsonPayload, String jsonPath) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(jsonPayload);
            String[] pathParts = jsonPath.split("\\.");
            
            // Navigate to parent node
            JsonNode parent = root;
            for (int i = 0; i < pathParts.length - 1; i++) {
                String part = pathParts[i];
                if (!parent.has(part)) {
                    // Path doesn't exist, nothing to remove
                    return jsonPayload;
                }
                parent = parent.get(part);
                if (parent == null || !(parent instanceof ObjectNode)) {
                    // Path segment is not an object, cannot remove from it
                    return jsonPayload;
                }
            }
            
            // Remove field at final path
            String finalKey = pathParts[pathParts.length - 1];
            if (parent instanceof ObjectNode && parent.has(finalKey)) {
                ((ObjectNode) parent).remove(finalKey);
            }
            
            return OBJECT_MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            Assert.fail("Failed to remove JSONPath '" + jsonPath + "' from payload: " + e.getMessage());
            throw new AssertionError("unreachable");
        }
    }

    /**
     * Resolves variables in path strings (e.g., "$BOOKING_ID").
     * Variables are replaced with their values from context.
     */
    private String resolveVariablesInPath(String path) {
        if (path == null || !path.contains("$")) {
            return path;
        }
        
        StringBuilder result = new StringBuilder();
        int start = 0;
        int dollarIndex = path.indexOf('$', start);
        
        while (dollarIndex >= 0) {
            result.append(path, start, dollarIndex);
            
            // Find the end of the variable name (end of string, or non-alphanumeric/underscore)
            int varEnd = dollarIndex + 1;
            while (varEnd < path.length() && 
                   (Character.isLetterOrDigit(path.charAt(varEnd)) || path.charAt(varEnd) == '_')) {
                varEnd++;
            }
            
            String varName = path.substring(dollarIndex, varEnd);
            try {
                String resolvedValue = resolveVariables(varName);
                result.append(resolvedValue);
            } catch (AssertionError e) {
                // Enhance error message with path context
                Assert.fail("Variable '" + varName + "' not found in context while resolving path '" + path + "'. " + e.getMessage());
            }
            
            start = varEnd;
            dollarIndex = path.indexOf('$', start);
        }
        
        result.append(path.substring(start));
        return result.toString();
    }
}