package com.shop.generic.purchasingservice.validators;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.request.RestTemplateUtil;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Validates that the request product ids are valid IDs and that the products exist This could have
 * been combined with the {@link ProductsCanBePurchasedValidator}, but I thought it would be a
 * slightly better separation of concerns to have different validators for it
 */
//TODO: Consider removing this validator, as if a product id is invalid, the product-service should just return that error message
//Doing it this way reduces the number of requests we have to make by 1

@Component
@Slf4j
public class ProductsExistValidator implements Validator<EnrichedPurchaseRequest> {

    private final String PRODUCTS_URI = "/products";

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    private final RestTemplateUtil restTemplateUtil;

    public ProductsExistValidator(final RestTemplateUtil restTemplateUtil) {
        this.restTemplateUtil = restTemplateUtil;
    }

    @Override
    public void validate(final EnrichedPurchaseRequest enrichedPurchaseRequest)
            throws Exception {
        final List<PurchaseProductDTO> purchaseVOs = enrichedPurchaseRequest.purchaseProductDTOList();
        log.info("Validating product ids are valid...");
        final List<Integer> productIds = purchaseVOs.stream().map(PurchaseProductDTO::productId)
                .toList();
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .path(PRODUCTS_URI)
                .queryParam("productIds", productIds).build();
        //We don't need to inspect the response, as if a product doesn't exist, a 400 will be returned, which will be caught and handled
        this.restTemplateUtil.getForObject(
                uri.toString(),
                new ParameterizedTypeReference<>() {
                });
        log.info("All product ids are valid");
    }
}
