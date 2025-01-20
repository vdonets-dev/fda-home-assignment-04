Feature: Retrieve all stored drug applications

  Scenario: Fetch all stored applications
    Given the database contains the following applications:
      | application_number | manufacturer_names | substance_names  | product_numbers |
      | ANDA203305         | ["Pfizer"]         | ["Atorvastatin"] | ["001"]         |
    When I send a GET request to "/api/drugs"
    Then the response status code should be 200
    And the response body should contain the following applications:
      | application_number | manufacturer_names | substance_names  | product_numbers |
      | ANDA203305         | ["Pfizer"]         | ["Atorvastatin"] | ["001"]         |
