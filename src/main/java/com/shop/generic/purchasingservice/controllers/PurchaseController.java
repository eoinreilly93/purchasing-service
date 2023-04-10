package com.shop.generic.purchasingservice.controllers;

import com.shop.generic.common.dtos.OrderResponseDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.purchasingservice.services.PurchasingService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    private final RestApiResponseFactory restApiResponseFactory;
    private final PurchasingService purchasingService;

    public PurchaseController(final RestApiResponseFactory restApiResponseFactory,
            final PurchasingService purchasingService) {
        this.restApiResponseFactory = restApiResponseFactory;
        this.purchasingService = purchasingService;
    }

    @PostMapping("/products")
    public ResponseEntity<RestApiResponse> purchaseProducts(
            @RequestBody final List<PurchaseProductDTO> purchaseProductDTOS)
            throws Exception {

        final RestApiResponse<OrderResponseDTO> response = this.purchasingService.purchaseProducts(
                purchaseProductDTOS);

        return ResponseEntity.ok(
                this.restApiResponseFactory.createSuccessResponse(response.getResult()));
    }
}
