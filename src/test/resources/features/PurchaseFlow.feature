Feature: Purchase service flow

  Background:
    Given initial setup is complete
    And the service is up and running
    And now is "2024-11-06T08:13:12.345"

  @Positive
  Scenario: Successfully create a purchase

    Given the "PRODUCTS" get API "/products?productIds=1" returns
    """
      {
        "message": null,
        "error": null,
        "result": [
          {
            "id": 1,
            "name": "PRODUCT 1",
            "price": 29.99,
            "stockStatus": "AVAILABLE",
            "stockCount": 100
          }
        ],
        "timestamp": "2024-11-19T19:43:58.065521"
      }
    """

    And the "PRODUCTS" post API "/products/update" returns
    """
      {
        "message": null,
        "error": null,
        "result": "Product stock successfully updated",
        "timestamp": "2024-11-19T19:48:45.205064"
      }
    """

    And the "ORDERS" post API "/orders" returns
    """
      {
        "message": null,
        "error": null,
        "result": {
          "orderId": "a3e16dc7-0c39-4e30-9d53-529bacf2f3e1",
          "status": "CREATED"
        },
        "timestamp": "2024-11-19T19:48:45.205064"
       }
    """

    When a POST request is sent to "/purchase/products" with data
    """
      [
        {
          "productId": 1,
          "quantity": 2,
          "price": 29.99
        }
      ]
    """

    Then a successful response is generated with a 200 status and a body similar to
    """
      {
        "message": null,
        "error": null,
        "result": {
          "orderId": "a3e16dc7-0c39-4e30-9d53-529bacf2f3e1",
          "status": "CREATED"
        },
        "timestamp": "2024-11-19T19:48:45.205064"
       }
    """

    And the purchase reservation is removed from the database