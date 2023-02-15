package com.shop.generic.purchasingservice.validators;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.util.List;
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
@Component
public class ProductsExistValidator implements Validator<List<PurchaseProductVO>> {

    private final String PRODUCTS_URI = "/products";

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    private final RestTemplateUtil restTemplateUtil;

    public ProductsExistValidator(final RestTemplateUtil restTemplateUtil) {
        this.restTemplateUtil = restTemplateUtil;
    }

    @Override
    public void validate(final List<PurchaseProductVO> purchaseVOs) throws ValidationException {

        final List<Integer> productIds = purchaseVOs.stream().map(PurchaseProductVO::productId)
                .toList();
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .path(PRODUCTS_URI)
                .queryParam("productIds", productIds).build();
        try {
            //We don't need to inspect the response, as if a product doesn't exist, a 400 will be returned, which will be caught and handled
            this.restTemplateUtil.getForObject(
                    uri.toString(),
                    new ParameterizedTypeReference<>() {
                    });
        } catch (final Exception e) {
            throw new ValidationException(e.getMessage());
        }
    }
}
