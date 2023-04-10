package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProductService {

    private static final String UPDATE_PRODUCT_URI = "/products/update";

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    private final RestTemplateUtil restTemplateUtil;
    private final ValidationService validationService;

    public ProductService(final RestTemplateUtil restTemplateUtil,
            final ValidationService validationService) {
        this.restTemplateUtil = restTemplateUtil;
        this.validationService = validationService;
    }

    //TODO: Parameter the response
    public RestApiResponse updateProductStock(final List<PurchaseProductDTO> purchaseProductDTOS)
            throws Exception {
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .path(UPDATE_PRODUCT_URI).build();
        return this.restTemplateUtil.postForObject(uri.toString(), purchaseProductDTOS,
                new ParameterizedTypeReference<>() {
                });
    }

    /**
     * Validate purchase is valid. Includes validating the quantity of items requested for purchase
     * are available, and also that the payment option is valid
     *
     * @param purchaseProductVOS
     */
    public void validatePurchaseIsValid(final EnrichedPurchaseRequest purchaseProductVOS)
            throws Exception {
        this.validationService.validate(purchaseProductVOS);
    }
}
