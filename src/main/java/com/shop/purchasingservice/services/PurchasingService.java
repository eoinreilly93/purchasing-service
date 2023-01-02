package com.shop.purchasingservice.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PurchasingService {

    //Perform validations
    // - Request to product-service to check stock for items
    // - Check payment is valid
    // - Update stock of item in database and make request to order-service to create the order
    // - Look into how this would be done if this service could not access the products table

    public ResponseEntity purchaseProduct() {
        return null;
    }
}
