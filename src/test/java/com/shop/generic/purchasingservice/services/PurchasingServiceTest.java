package com.shop.generic.purchasingservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.shop.generic.common.dtos.OrderResponseDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.enums.OrderStatus;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchasingServiceTest {

    @Mock
    private ProductService productService;

    @Mock
    private ReserveProductService reserveProductService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PurchasingService purchasingService;

    private List<PurchaseProductDTO> purchaseProductDTOS;

    @BeforeEach
    void setUp() {
        purchaseProductDTOS = new ArrayList<>();
        purchaseProductDTOS.add(
                new PurchaseProductDTO(1, 2, new BigDecimal("9.99")));
        purchaseProductDTOS.add(
                new PurchaseProductDTO(2, 3, new BigDecimal("14.99")));
    }

    @DisplayName("Verify you can create a valid purchase order")
    @Test
    void testPurchaseProducts_Success() throws Exception {
        final UUID mockPurchaseId = UUID.randomUUID();
        final OrderResponseDTO orderResponseDTO = new OrderResponseDTO(mockPurchaseId,
                OrderStatus.CREATED);
        final LocalDateTime now = LocalDateTime.now();
        final RestApiResponse<OrderResponseDTO> mockResponse = RestApiResponse.<OrderResponseDTO>builder()
                .result(orderResponseDTO)
                .message("Order created successfully")
                .timestamp(now)
                .build();

        // Mock response for updateProductStock
        final RestApiResponse mockProductServiceResponse = RestApiResponse.builder()
                .message("Product stock updated successfully")
                .timestamp(now)
                .build();

        // Mock the service method calls
        doNothing().when(reserveProductService).reserveProducts(any(UUID.class), anyList());
        doNothing().when(productService)
                .validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));
        when(productService.updateProductStock(anyList())).thenReturn(mockProductServiceResponse);
        when(orderService.createOrder(anyList())).thenReturn(mockResponse);

        final RestApiResponse<OrderResponseDTO> response = purchasingService.purchaseProducts(
                purchaseProductDTOS);

        // Verify interactions and assertions
        verify(reserveProductService).reserveProducts(any(UUID.class), eq(purchaseProductDTOS));
        verify(productService).validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));
        verify(productService).updateProductStock(purchaseProductDTOS);
        verify(orderService).createOrder(purchaseProductDTOS);
        verify(reserveProductService).deleteProductReservation(any(UUID.class));

        assertNotNull(response);
        assertEquals("Order created successfully", response.getMessage());
        assertNull(response.getError());
        assertNotNull(response.getTimestamp());
        assertEquals(mockResponse.getResult(), response.getResult());
    }

    @DisplayName("Verify you cannot create a purchase order with no products")
    @Test
    void testPurchaseProducts_EmptyProductList() {
        final List<PurchaseProductDTO> emptyProductList = new ArrayList<>();

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            purchasingService.purchaseProducts(emptyProductList);
        });

        assertEquals("Cannot create a purchase order wth no products", exception.getMessage());
        verifyNoInteractions(productService, reserveProductService, orderService);
    }

    @DisplayName("Verify a purchase order cannot be created if there is not enough stock")
    @Test
    void testPurchaseProducts_ProductStockValidationFails() throws Exception {
        doNothing().when(reserveProductService).reserveProducts(any(UUID.class), anyList());
        doThrow(new RuntimeException("Stock validation failed")).when(productService)
                .validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            purchasingService.purchaseProducts(purchaseProductDTOS);
        });

        assertEquals("Stock validation failed", exception.getMessage());

        verify(reserveProductService).reserveProducts(any(UUID.class), eq(purchaseProductDTOS));
        verify(productService).validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));
        verify(reserveProductService).deleteProductReservation(any(UUID.class));
        verify(orderService, never()).createOrder(anyList());
        verify(productService, never()).updateProductStock(anyList());
    }

    @DisplayName("Will return an error message if it fails to create the order")
    @Test
    void testPurchaseProducts_ExceptionDuringOrderCreation() throws Exception {

        // Mock the response for reserveProducts
        doNothing().when(reserveProductService).reserveProducts(any(UUID.class), anyList());

        // Mock the response for validatePurchaseIsValid
        doNothing().when(productService)
                .validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));

        // Mock the response for updateProductStock
        final RestApiResponse mockUpdateResponse = RestApiResponse.builder()
                .message("Stock updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
        when(productService.updateProductStock(anyList())).thenReturn(mockUpdateResponse);

        // Mock the behavior to throw an exception for createOrder
        when(orderService.createOrder(anyList()))
                .thenThrow(new RuntimeException("Order creation failed"));

        // Mock the deleteProductReservation to do nothing
        doNothing().when(reserveProductService).deleteProductReservation(any(UUID.class));

        // Execute the method and expect an exception
        final Exception exception = assertThrows(RuntimeException.class, () -> {
            purchasingService.purchaseProducts(purchaseProductDTOS);
        });

        // Verify interactions
        verify(reserveProductService).reserveProducts(any(UUID.class), eq(purchaseProductDTOS));
        verify(productService).validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));
        verify(productService).updateProductStock(eq(purchaseProductDTOS));
        verify(orderService).createOrder(purchaseProductDTOS);
        verify(reserveProductService).deleteProductReservation(any(UUID.class));

        // Assert that the exception thrown is the correct one
        assertEquals("Order creation failed", exception.getMessage());
    }

    @DisplayName("Product reservation should be deleted even if an exception occurs")
    @Test
    void testPurchaseProducts_DeleteProductReservationAlwaysCalled() throws Exception {
        doNothing().when(reserveProductService).reserveProducts(any(UUID.class), anyList());
        doThrow(new RuntimeException("Stock validation failed")).when(productService)
                .validatePurchaseIsValid(any(EnrichedPurchaseRequest.class));

        assertThrows(RuntimeException.class, () -> {
            purchasingService.purchaseProducts(purchaseProductDTOS);
        });

        verify(reserveProductService).deleteProductReservation(any(UUID.class));
    }
}
