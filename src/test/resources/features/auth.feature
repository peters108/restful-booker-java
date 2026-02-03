Feature: Authentication - Create Token

  Scenario: Successfully create token with valid credentials
    Given I have valid credentials
    When I send "POST /auth" request
    Then the response HTTP code is 200
    	And the response contains a valid token
    	And the token is a non-empty string

  Scenario: Create token with admin credentials
    Given I have admin credentials
    When I send "POST /auth" request
    Then the response HTTP code is 200
    	And the response body matches the schema "schemas/token-response-schema.json"

  Scenario: Fail to create token with invalid credentials
    Given I have invalid credentials
    When I send "POST /auth" request
    Then the response HTTP code is 200
    	And JSONpath "reason" value is "Bad credentials"
