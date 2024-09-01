package com.shop.generic.purchasingservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import com.shop.generic.purchasingservice.util.RestTemplateUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestTemplateUtil restTemplateUtil;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // Set productServiceUrl using ReflectionTestUtils since it's a private field
        ReflectionTestUtils.setField(productService, "productServiceUrl", "http://localhost:8080");
    }

    @Test
    void testUpdateProductStock_Success() throws Exception {
        // Given
        final List<PurchaseProductDTO> purchaseProductDTOS = List.of(
                new PurchaseProductDTO(1, 5, new BigDecimal("100.0")),
                new PurchaseProductDTO(2, 2, new BigDecimal("50.0"))
        );
        final RestApiResponse expectedResponse = RestApiResponse.builder()
                .message("Stock updated successfully")
                .build();

        final String expectedUri = UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
                .path("/products/update").build().toString();

        when(restTemplateUtil.postForObject(eq(expectedUri), eq(purchaseProductDTOS),
                any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // When
        final RestApiResponse actualResponse = productService.updateProductStock(
                purchaseProductDTOS);

        // Then
        assertEquals(expectedResponse, actualResponse);
        verify(restTemplateUtil, times(1))
                .postForObject(eq(expectedUri), eq(purchaseProductDTOS),
                        any(ParameterizedTypeReference.class));
    }

    @Test
    void testUpdateProductStock_ExceptionThrown() throws Exception {
        // Given
        final List<PurchaseProductDTO> purchaseProductDTOS = List.of(
                new PurchaseProductDTO(1, 5, new BigDecimal("100.0")),
                new PurchaseProductDTO(2, 2, new BigDecimal("50.0"))
        );

        final String expectedUri = UriComponentsBuilder.fromHttpUrl("http://localhost:8080")
                .path("/products/update").build().toString();

        when(restTemplateUtil.postForObject(eq(expectedUri), eq(purchaseProductDTOS),
                any(ParameterizedTypeReference.class)))
                .thenThrow(new Exception("Service unavailable"));

        // When
        final Exception exception = assertThrows(Exception.class, () -> {
            productService.updateProductStock(purchaseProductDTOS);
        });

        // Then
        assertEquals("Service unavailable", exception.getMessage());
        verify(restTemplateUtil, times(1))
                .postForObject(eq(expectedUri), eq(purchaseProductDTOS),
                        any(ParameterizedTypeReference.class));
    }

    @Test
    void testValidatePurchaseIsValid_Success() throws Exception {
        // Given
        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(
                UUID.randomUUID(), List.of(
                new PurchaseProductDTO(1, 5, new BigDecimal("10.50")),
                new PurchaseProductDTO(2, 2, new BigDecimal("9.99"))
        ));

        when(validationService.validate(enrichedPurchaseRequest)).thenReturn(true);

        // When
        productService.validatePurchaseIsValid(enrichedPurchaseRequest);

        // Then
        verify(validationService, times(1)).validate(enrichedPurchaseRequest);
    }

    @Test
    void testValidatePurchaseIsValid_ThrowsException() throws Exception {
        // Given
        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(
                UUID.randomUUID(), List.of(
                new PurchaseProductDTO(1, 5, new BigDecimal("10.50")),
                new PurchaseProductDTO(2, 2, new BigDecimal("9.99"))
        ));

        doThrow(new Exception("Invalid purchase")).when(validationService)
                .validate(enrichedPurchaseRequest);

        // When
        final Exception exception = assertThrows(Exception.class, () -> {
            productService.validatePurchaseIsValid(enrichedPurchaseRequest);
        });

        // Then
        assertEquals("Invalid purchase", exception.getMessage());
        verify(validationService, times(1)).validate(enrichedPurchaseRequest);
    }
}