package com.shop.purchasingservice.services;

import com.shop.generic.common.valueobjects.PurchaseProductVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

    private static final String UPDATE_PRODUCT_URI = "/products/update";

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    private final RestTemplate restTemplate;

    public ProductService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity updateProductStock(final List<PurchaseProductVO> purchaseProductVOS) {
        return restTemplate.postForEntity(productServiceUrl + UPDATE_PRODUCT_URI,
                purchaseProductVOS, String.class);
    }
}
