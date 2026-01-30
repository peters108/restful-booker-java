Feature: Authentication - Create Token

  Scenario: Successfully create token with valid credentials
    Given I have valid credentials
    When I send a POST request to create token
    Then the response HTTP code is 200
    And the response contains a valid token
    And the token is a non-empty string

  Scenario: Create token with custom credentials
    Given I have credentials with username "admin" and password "password123"
    When I send a POST request to create token
    Then the response HTTP code is 200
    And the response body matches the schema "schemas/token-response-schema.json"

  Scenario: Fail to create token with invalid credentials
    Given I have credentials with username "invalid" and password "invalid"
    When I send a POST request to create token
    Then the response HTTP code is 200
    And JSONpath "reason" value is "Bad credentials"
