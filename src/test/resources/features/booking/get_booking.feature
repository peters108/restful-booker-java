Feature: GET /booking

  # ========== GetBookingIds - Positive ==========

  Scenario: Get all booking IDs
    When I send "GET /booking" request
    Then the response HTTP code is 200
    	And the response body matches the schema "schemas/get_booking.json"
    	And JSONpath "$" is not empty

  Scenario: Get booking IDs filtered by firstname
    When I send "GET /booking?firstname=Jim" request
    Then the response HTTP code is 200
    	And the response body matches the schema "schemas/get_booking.json"

  # ========== GetBookingIds - Negative ==========

  Scenario: Get booking IDs with non-matching filter returns empty array
    When I send "GET /booking?firstname=NonexistentName12345" request
    Then the response HTTP code is 200
    	And JSONpath "$" is empty array
