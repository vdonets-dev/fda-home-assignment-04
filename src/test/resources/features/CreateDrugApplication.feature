Feature: Create a new drug application

  Scenario: Successfully create a drug application
    When I send a POST request to "/api/drugs" with the following payload:
      """
      {
        "application_number": "ANDA203300",
        "manufacturer_names": ["Pfizer", "Hikma"],
        "substance_names": ["Atorvastatin", "Simvastatin"],
        "product_numbers": ["001", "002"]
      }
      """
    Then the response status code should be 201
    And the database should contain the following drug applications:
      | application_number | manufacturer_names         | substance_names         | product_numbers |
      | ANDA203300         | ["Pfizer", "Hikma"]       | ["Atorvastatin", "Simvastatin"] | ["001", "002"] |

  Scenario: Validation error on invalid request
    When I send a POST request to "/api/drugs" with the following payload:
    """
    {
      "application_number": "",
      "manufacturer_names": ["Producer"],
      "substance_names": ["Subs"],
      "product_numbers": ["PR-1"]
    }
    """
    Then the response status code should be 400
    And the response body should contain the following error messages:
      | applicationNumber: size must be between 1 and 100 |
      | applicationNumber: must match '^[A-Za-z0-9_-]+$' |
