Feature: Booking API - CreateBooking, GetBookingIds, GetBooking

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

  # ========== GetBooking - Positive ==========

  Scenario: Get existing booking by ID
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    And I save JSONpath "bookingid" value as "$BOOKING_ID"
    When I send "GET /booking/$BOOKING_ID" request
    Then the response HTTP code is 200
    And the response body matches the schema "schemas/get_booking_id.json"

  # ========== GetBooking - Negative ==========

  Scenario: Get non-existent booking returns 404
    When I send "GET /booking/999999999" request
    Then the response HTTP code is 404
