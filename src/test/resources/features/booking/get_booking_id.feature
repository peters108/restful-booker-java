Feature: GET /booking/{id}

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
