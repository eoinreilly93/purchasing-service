package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProductService {

    private static final String UPDATE_PRODUCT_URI = "/products/update";
    private static final String CHECK_PRODUCT_AVAILABLE_URI = "/products/{productId}/available";

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    private final RestTemplate restTemplate;
    private final RestTemplateUtil restTemplateUtil;
    private final ValidationService validationService;

    public ProductService(final RestTemplate restTemplate,
            final RestTemplateUtil restTemplateUtil, final ValidationService validationService) {
        this.restTemplate = restTemplate;
        this.restTemplateUtil = restTemplateUtil;
        this.validationService = validationService;
    }

    public RestApiResponse updateProductStock(final List<PurchaseProductVO> purchaseProductVOS)
            throws Exception {
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .path(UPDATE_PRODUCT_URI).build();
        //TODO: Fix the ParameterizedTypeReference type here
        return this.restTemplateUtil.postForObject(uri.toString(), purchaseProductVOS,
                new ParameterizedTypeReference<>() {
                });
    }

    public String checkIfProductIsAvailable(final int productId) throws Exception {
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .path(CHECK_PRODUCT_AVAILABLE_URI)
                .pathSegment(String.valueOf(productId)).build();
        final RestApiResponse<String> response = restTemplateUtil.getForObject(uri.toString(),
                new ParameterizedTypeReference<RestApiResponse<String>>() {
                });
        return response.getResult();

    }


    /**
     * Validate purchase is valid. Includes validating the quantity of items requested for purchase
     * are available, and also that the payment option is valid
     *
     * @param purchaseProductVOS
     */
    public void validatePurchaseIsValid(final EnrichedPurchaseRequest purchaseProductVOS)
            throws ValidationException {
        this.validationService.validate(purchaseProductVOS);
    }
}
