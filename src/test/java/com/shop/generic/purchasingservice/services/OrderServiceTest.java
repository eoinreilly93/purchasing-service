package com.shop.generic.purchasingservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.generic.common.dtos.OrderCreationDTO;
import com.shop.generic.common.dtos.OrderResponseDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.enums.OrderStatus;
import com.shop.generic.common.rest.request.RestTemplateUtil;
import com.shop.generic.common.rest.response.RestApiResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String ORDER_SERVICE_URL = "http://localhost:8080";
    private static final String CREATE_ORDER_URI = "/orders";

    @Mock
    private RestTemplateUtil restTemplateUtil;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Set the private field 'orderServiceUrl' using ReflectionTestUtils
        ReflectionTestUtils.setField(orderService, "orderServiceUrl", ORDER_SERVICE_URL);
    }

    @Test
    void testCreateOrder_Successful() throws Exception {
        // Arrange
        final PurchaseProductDTO product1 = new PurchaseProductDTO(1, 2, new BigDecimal("10.00"));
        final PurchaseProductDTO product2 = new PurchaseProductDTO(2, 1, new BigDecimal("15.50"));
        final List<PurchaseProductDTO> purchaseProductDTOS = List.of(product1, product2);

        final OrderCreationDTO expectedOrderCreationDTO = new OrderCreationDTO(purchaseProductDTOS,
                "London");
        final OrderResponseDTO orderResponseDTO = new OrderResponseDTO(UUID.randomUUID(),
                OrderStatus.CREATED);
        final RestApiResponse<OrderResponseDTO> expectedResponse = RestApiResponse.<OrderResponseDTO>builder()
                .message("Order created successfully")
                .result(orderResponseDTO)
                .timestamp(LocalDateTime.now())
                .build();

        when(restTemplateUtil.postForObject(any(String.class), any(OrderCreationDTO.class),
                any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Act
        final RestApiResponse<OrderResponseDTO> actualResponse = orderService.createOrder(
                purchaseProductDTOS);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());
        assertNotNull(actualResponse.getTimestamp());

        final ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<OrderCreationDTO> dtoCaptor = ArgumentCaptor.forClass(
                OrderCreationDTO.class);

        verify(restTemplateUtil, times(1)).postForObject(
                urlCaptor.capture(),
                dtoCaptor.capture(),
                any(ParameterizedTypeReference.class)
        );

        final UriComponents expectedUri = UriComponentsBuilder.fromHttpUrl(ORDER_SERVICE_URL)
                .path(CREATE_ORDER_URI).build();

        assertEquals(expectedUri.toString(), urlCaptor.getValue());
        assertEquals(expectedOrderCreationDTO, dtoCaptor.getValue());
    }
}
