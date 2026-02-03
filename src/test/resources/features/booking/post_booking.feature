Feature: POST /booking

  # ========== CreateBooking - Positive ==========

  Scenario: Create booking with all fields
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    	And the response body matches the schema "schemas/post_booking.json"
    	And JSONpath "bookingid" has value

  Scenario: Create booking without additionalneeds
    Given I have API endpoint: "POST /booking"
    	And I have request payload from file: "post_booking.json"
    	And I remove JSONpath "additionalneeds" from payload
    When I call the API
    Then the response HTTP code is 200
    	And JSONpath "bookingid" has value

  # ========== CreateBooking - Negative ==========

  Scenario: Create booking with empty body fails
    When I send "POST /booking" request with the following payload:
      """
      {}
      """
    Then the response HTTP code is 500

  Scenario Outline: Create booking without required field fails
    Given I have API endpoint: "POST /booking"
    	And I have request payload from file: "post_booking.json"
    	And I remove JSONpath "<field>" from payload
    When I call the API
    Then the response HTTP code is 500

    Examples:
      | field        |
      | firstname    |
      | bookingdates |
