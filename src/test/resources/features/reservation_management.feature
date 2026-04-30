Feature: Reservation Management
  As a user of the booking platform
  I want to manage reservations
  So that I can book, update, cancel and view reservations

  Scenario: Create a new reservation
    Given a room is available for booking
    And a guest exists
    When the user creates a reservation with valid details
    Then the reservation should be successfully created

  Scenario: View a reservation by ID
    Given a reservation exists with a specific ID
    When the user requests the reservation by that ID
    Then the reservation details should be returned

  Scenario: Update an existing reservation
    Given an existing reservation
    When the user updates the reservation with new details
    Then the reservation should be successfully updated

  Scenario: Cancel an existing reservation
    Given an existing reservation
    When the user cancels the reservation
    Then the reservation should be successfully deleted

  Scenario: Attempt to create a reservation with overlapping dates
    Given the room is already reserved for those dates
    When I attempt to create a reservation for the same room and dates
    Then the reservation should be rejected with an overlap error

  Scenario: Attempt to create a reservation exceeding room capacity
    Given a room with capacity 2
    When I attempt to create a reservation for 5 guests
    Then the reservation should be rejected with a capacity error
