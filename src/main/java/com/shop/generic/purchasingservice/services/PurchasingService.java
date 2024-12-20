package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.dtos.OrderStatusDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PurchasingService {

    private final ProductService productService;
    private final ReserveProductService reserveProductService;
    private final OrderService orderService;

    public PurchasingService(final ProductService productService,
            final ReserveProductService reserveProductService, final OrderService orderService) {
        this.productService = productService;
        this.reserveProductService = reserveProductService;
        this.orderService = orderService;
    }

    //Perform validations
    // - Request to product-service to check stock for items
    // - Check payment is valid
    // - Update stock of item in database and make request to order-service to create the order
    // - TODO: Look into how this would be done if this service could not access the products table

    public RestApiResponse<OrderStatusDTO> purchaseProducts(
            final List<PurchaseProductDTO> purchaseProductDTOS)
            throws Exception {

        if (purchaseProductDTOS.isEmpty()) {
            throw new RuntimeException("Cannot create a purchase order wth no products");
        }

        //Possibly need to generate unique purchase id here to store with product reservations
        final UUID id = UUID.randomUUID();

        //Reserve the products
        this.reserveProductService.reserveProducts(id, purchaseProductDTOS);

        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(id,
                purchaseProductDTOS);

        try {
            //Get the quantity of each product from the product-service and compare to the reserved
            //amount to make sure there is enough available
            this.productService.validatePurchaseIsValid(enrichedPurchaseRequest);

            //TODO: We should only invoke this if the order-service returns a 200 for the creation response really. Shouldn't update stock until order is created
            //If the above condition is met, consider the product(s) purchased and update the product stock in the product-service
            this.productService.updateProductStock(
                    purchaseProductDTOS);

            return this.orderService.createOrder(
                    purchaseProductDTOS);

        }
        // We need to delete the product reservations whether the purchase is successful or not
        finally {
            this.reserveProductService.deleteProductReservation(id);
        }
    }
}
