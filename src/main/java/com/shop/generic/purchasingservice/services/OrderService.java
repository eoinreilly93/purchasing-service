package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.dtos.OrderCreationDTO;
import com.shop.generic.common.dtos.OrderStatusDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.request.RestTemplateUtil;
import com.shop.generic.common.rest.response.RestApiResponse;
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

    private static final String CREATE_ORDER_URI = "/orders";

    @Value("${services.order-service.url}")
    private String orderServiceUrl;

    private final RestTemplateUtil restTemplateUtil;

    public OrderService(final RestTemplateUtil restTemplateUtil) {
        this.restTemplateUtil = restTemplateUtil;
    }

    public RestApiResponse<OrderStatusDTO> createOrder(
            final List<PurchaseProductDTO> purchaseProductDTOS)
            throws Exception {

        //TODO: Replace this eventually by fetching the address from the current user session
        final String city = "London";

        final OrderCreationDTO orderCreationDTO = new OrderCreationDTO(purchaseProductDTOS, city);
        final UriComponents uri = UriComponentsBuilder.fromHttpUrl(orderServiceUrl)
                .path(CREATE_ORDER_URI).build();
        return this.restTemplateUtil.postForObject(uri.toString(), orderCreationDTO,
                new ParameterizedTypeReference<>() {
                });
    }
}
