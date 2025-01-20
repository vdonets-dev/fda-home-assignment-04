Feature: Search drug applications by manufacturer name

  Scenario: Validate search results by manufacturer name
    Given the OpenFDA API is available for manufacturer "Hikma" with response "/api/stubs/search-api-stub.json"
    When I send a GET request to "/api/drugs/search" with the following query parameters:
      | manufacturer_name | Hikma |
      | page              | 0     |
      | size              | 10    |
    Then the response status code should be 200
    And the response body for search should contain:
      | meta.skip  | 0   |
      | meta.limit | 10  |
      | meta.total | 336 |
    And the response should include only applications with:
      | manufacturer_name |
      | Hikma             |
