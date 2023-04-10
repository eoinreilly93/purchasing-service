package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.dtos.OrderCreationDTO;
import com.shop.generic.common.dtos.OrderResponseDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class OrderService {

    private static final String CREATE_ORDER_URI = "/orders/create";

    @Value("${services.order-service.url}")
    private String orderServiceUrl;

    private final RestTemplateUtil restTemplateUtil;

    public OrderService(final RestTemplateUtil restTemplateUtil) {
        this.restTemplateUtil = restTemplateUtil;
    }

    public RestApiResponse<OrderResponseDTO> createOrder(
            final List<PurchaseProductDTO> purchaseProductDTOS)
            throws Exception {

        final OrderCreationDTO orderCreationDTO = new OrderCreationDTO(purchaseProductDTOS);
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(orderServiceUrl)
                .path(CREATE_ORDER_URI).build();
        return this.restTemplateUtil.postForObject(uri.toString(), orderCreationDTO,
                new ParameterizedTypeReference<>() {
                });
    }
}
