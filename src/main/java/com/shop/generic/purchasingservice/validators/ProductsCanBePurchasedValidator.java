package com.shop.generic.purchasingservice.validators;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.valueobjects.ProductVO;
import com.shop.generic.common.valueobjects.PurchaseProductVO;
import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import com.shop.generic.purchasingservice.repositories.ProductPurchaseReserveRepository;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProductsCanBePurchasedValidator implements Validator<EnrichedPurchaseRequest> {

    private final String PRODUCTS_URI = "/products";

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    private final RestTemplateUtil restTemplateUtil;
    private final ProductPurchaseReserveRepository productPurchaseReserveRepository;

    public ProductsCanBePurchasedValidator(final RestTemplateUtil restTemplateUtil,
            final ProductPurchaseReserveRepository productPurchaseReserveRepository) {
        this.restTemplateUtil = restTemplateUtil;
        this.productPurchaseReserveRepository = productPurchaseReserveRepository;
    }

    @Override
    public void validate(final EnrichedPurchaseRequest enrichedPurchaseRequest)
            throws ValidationException {
        final List<Integer> productIds = enrichedPurchaseRequest.purchaseProductVOList().stream()
                .map(PurchaseProductVO::productId)
                .toList();
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(productServiceUrl)
                .path(PRODUCTS_URI)
                .queryParam("productIds", productIds).build();
        try {
            //We don't need to inspect the response, as if a product doesn't exist, a 400 will be returned, which will be caught and handled
            final RestApiResponse<List<ProductVO>> response = this.restTemplateUtil.getForObject(
                    uri.toString(),
                    new ParameterizedTypeReference<>() {
                    });
            final List<ProductVO> productVOSResponse = response.getResult();

            canProductsBePurchased(enrichedPurchaseRequest, productVOSResponse);
        } catch (final Exception e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private void canProductsBePurchased(final EnrichedPurchaseRequest enrichedPurchaseRequest,
            final List<ProductVO> productVOSResponse)
            throws ValidationException {

        //List all products
        for (final PurchaseProductVO requestedProductToPurchase : enrichedPurchaseRequest.purchaseProductVOList()) {

            //List all reserved products in DB excluding ones for this purchase id
            final List<ProductPurchaseReserve> reservedProductsInDB = this.productPurchaseReserveRepository.findAllByProductId(
                            requestedProductToPurchase.productId()).stream()
                    .filter(reservation -> reservation.getPurchaseId()
                            != enrichedPurchaseRequest.purchaseId()).toList();

            final int quantityOfProductReservedForOtherUsers = reservedProductsInDB.stream()
                    .mapToInt(
                            ProductPurchaseReserve::getQuantity).sum();

            final int quantityOfProductRequestedForCurrentUser = requestedProductToPurchase.quantity();

            final int quantityOfProductAvailableToPurchase = productVOSResponse.stream()
                    .filter(product -> product.id() == requestedProductToPurchase.productId())
                    .toList().get(0).stockCount();

            if (!productCanBePurchased(quantityOfProductRequestedForCurrentUser,
                    quantityOfProductAvailableToPurchase, quantityOfProductReservedForOtherUsers)) {
                throw new ValidationException(
                        "Purchase not valid as product " + requestedProductToPurchase.productId()
                                + " does not have sufficient stock");
            }
        }
    }

    /**
     * Calculate if the product can be purchased
     *
     * @param purchaseAmount the amount of product to be purchased
     * @param stockAmount    the amount of product available for purchase
     * @param reservedAmount the amount of product currently reserved and not available for
     *                       purchase
     * @return true if the product can be purchased
     */
    private boolean productCanBePurchased(final int purchaseAmount, final int stockAmount,
            final int reservedAmount) {
        return purchaseAmount <= stockAmount - reservedAmount;
    }
}
