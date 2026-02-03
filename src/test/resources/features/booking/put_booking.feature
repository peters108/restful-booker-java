Feature: PUT /booking/{id}

  # ========== UpdateBooking - Positive ==========

  Scenario: Update existing booking with all fields
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    	And I save JSONpath "bookingid" value as "$BOOKING_ID"
    Given I am authenticated
    When I send "PUT /booking/$BOOKING_ID" request with payload from file "put_booking.json"
    Then the response HTTP code is 200
    	And the response body matches the schema "schemas/get_booking_id.json"
    	And JSONpath "firstname" value is "Jane"
    	And JSONpath "lastname" value is "Doe"

  # ========== UpdateBooking - Negative ==========

  Scenario: Update booking without authentication returns 403
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    	And I save JSONpath "bookingid" value as "$BOOKING_ID"
    When I send "PUT /booking/$BOOKING_ID" request with payload from file "put_booking.json"
    Then the response HTTP code is 403

  Scenario: Update non-existent booking returns 405
    Given I am authenticated
    When I send "PUT /booking/999999999" request with payload from file "put_booking.json"
    Then the response HTTP code is 405
