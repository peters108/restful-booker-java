package com.restfulbooker.parameters;

import io.cucumber.java.ParameterType;

/**
 * Custom Cucumber parameter types for step definitions.
 * All types here are registered globally and can be used in any step (e.g. {ofType}).
 */
public class ParameterTypes {

    @ParameterType(name = "ofType", value = "string|number|boolean")
    public static String ofType(String raw) {
        return raw;
    }

    /**
     * Accepts multiple value types in Gherkin without forcing quotes on everything.
     * Matches:
     *   - Quoted strings: "John", "hello world"
     *   - Variables: $ID, $WHATEVER
     *   - Numbers: 50, 19.99
     *   - Booleans: true, false
     */
    @ParameterType(
        name = "value",
        value = "\"[^\"]*\"|\\$[A-Z_]+|[0-9]+\\.?[0-9]*|true|false"
    )
    public static String value(String raw) {
        // Remove surrounding quotes if present
        if (raw.startsWith("\"") && raw.endsWith("\"")) {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }
}