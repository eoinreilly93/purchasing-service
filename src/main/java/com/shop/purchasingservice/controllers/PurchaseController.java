package com.shop.purchasingservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("purchase/")
public class PurchaseController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("products/")
    public ResponseEntity purchaseProducts() {
        return null;
    }

}
