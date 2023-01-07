package com.shop.purchasingservice.services;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

@Service
public class PurchasingService {

    private final ProductService productService;

    public PurchasingService(final ProductService productService) {
        this.productService = productService;
    }

    //Perform validations
    // - Request to product-service to check stock for items
    // - Check payment is valid
    // - Update stock of item in database and make request to order-service to create the order
    // - Look into how this would be done if this service could not access the products table

    public ResponseEntity purchaseProducts(final List<PurchaseProductVO> purchaseProductVOS) {
        try {
            return this.productService.updateProductStock(
                    purchaseProductVOS);
        }
        //TODO: Handle this with a ResponseHandler
        catch (final HttpStatusCodeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        //If 200 OK, create an order
    }

    //TODO: Undo the refactor to move ProductVO value objects to common. It shouldn't be needed if that service handles everything
    //related to products. This service just cares about what response it gets back to API calls to that service
}
