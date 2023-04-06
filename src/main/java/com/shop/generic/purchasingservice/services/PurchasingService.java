package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PurchasingService {

    private final ProductService productService;
    private final ReserveProductService reserveProductService;

    public PurchasingService(final ProductService productService,
            final ReserveProductService reserveProductService) {
        this.productService = productService;
        this.reserveProductService = reserveProductService;
    }

    //Perform validations
    // - Request to product-service to check stock for items
    // - Check payment is valid
    // - Update stock of item in database and make request to order-service to create the order
    // - Look into how this would be done if this service could not access the products table

    public ResponseEntity purchaseProducts(final List<PurchaseProductDTO> purchaseProductDTOS)
            throws Exception {

        //Possibly need to generate unique purchase id here to store with product reservations
        final UUID id = UUID.randomUUID();

        //Reserve the products
        this.reserveProductService.reserveProducts(id, purchaseProductDTOS);

        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(id,
                purchaseProductDTOS);

        //Get the quantity of each product from the product-service and compare to the reserved
        //amount to make sure there is enough available
        this.productService.validatePurchaseIsValid(enrichedPurchaseRequest);

        //If the above condition is met, consider the product(s) purchased and update the product stock in the product-service
        this.productService.updateProductStock(
                purchaseProductDTOS);

        //Delete order reservation
        this.reserveProductService.deleteProductReservation(id);

        //Create Order
        return ResponseEntity.ok("Order created");

        //If 200 OK, create an order
    }
}
