package com.shop.purchasingservice.controllers;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.purchasingservice.rest.response.RestApiResponse;
import com.shop.purchasingservice.rest.response.RestApiResponseFactory;
import com.shop.purchasingservice.services.PurchasingService;
import java.util.List;
import org.springframework.http.HttpStatus;
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
            @RequestBody final List<PurchaseProductVO> purchaseProductVOS) {

        final ResponseEntity response = this.purchasingService.purchaseProducts(purchaseProductVOS);

        //TODO: Replace this message with and OrderCreatedVO
        return new ResponseEntity<>(
                restApiResponseFactory.createSuccessResponse(response.getBody()),
                HttpStatus.CREATED);
    }
}
