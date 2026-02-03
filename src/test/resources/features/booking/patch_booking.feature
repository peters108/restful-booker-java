Feature: PATCH /booking/{id}

  # ========== PartialUpdateBooking - Positive ==========

  Scenario: Partial update booking - change firstname only
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    	And I save JSONpath "bookingid" value as "$BOOKING_ID"
    Given I am authenticated
    When I send "PATCH /booking/$BOOKING_ID" request with the following payload:
      """
      {"firstname": "UpdatedName"}
      """
    Then the response HTTP code is 200
    	And JSONpath "firstname" value is "UpdatedName"
    	And JSONpath "lastname" value is "Brown"

  Scenario: Partial update booking - change multiple fields
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    	And I save JSONpath "bookingid" value as "$BOOKING_ID"
    Given I am authenticated
    When I send "PATCH /booking/$BOOKING_ID" request with the following payload:
      """
      {"firstname": "Partial", "totalprice": 999}
      """
    Then the response HTTP code is 200
    	And JSONpath "firstname" value is "Partial"
    	And JSONpath "totalprice" value is 999

  # ========== PartialUpdateBooking - Negative ==========

  Scenario: Partial update booking without authentication returns 403
    When I send "POST /booking" request with payload from file "post_booking.json"
    Then the response HTTP code is 200
    	And I save JSONpath "bookingid" value as "$BOOKING_ID"
    When I send "PATCH /booking/$BOOKING_ID" request with the following payload:
      """
      {"firstname": "Unauthorized"}
      """
    Then the response HTTP code is 403

  Scenario: Partial update non-existent booking returns 405
    Given I am authenticated
    When I send "PATCH /booking/999999999" request with the following payload:
      """
      {"firstname": "NotFound"}
      """
    Then the response HTTP code is 405
