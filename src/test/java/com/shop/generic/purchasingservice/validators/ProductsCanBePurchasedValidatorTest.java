package com.shop.generic.purchasingservice.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shop.generic.common.dtos.ProductDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.entities.Product;
import com.shop.generic.common.enums.StockStatus;
import com.shop.generic.common.rest.exceptions.ValidationException;
import com.shop.generic.common.rest.request.RestTemplateUtil;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.purchasingservice.models.EnrichedPurchaseRequest;
import com.shop.generic.purchasingservice.repositories.ProductPurchaseReserveRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class ProductsCanBePurchasedValidatorTest {

    @Mock
    private RestTemplateUtil restTemplateUtil;

    @Mock
    private ProductPurchaseReserveRepository productPurchaseReserveRepository;

    @InjectMocks
    private ProductsCanBePurchasedValidator productsCanBePurchasedValidator;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    @Captor
    private ArgumentCaptor<ParameterizedTypeReference<RestApiResponse<List<ProductDTO>>>> responseTypeCaptor;

    @BeforeEach
    void setUp() {
        productsCanBePurchasedValidator = new ProductsCanBePurchasedValidator(restTemplateUtil,
                productPurchaseReserveRepository);
        ReflectionTestUtils.setField(productsCanBePurchasedValidator, "productServiceUrl",
                "http://localhost:8080");
    }

    @Test
    void testValidate_Success() throws Exception {
        // given
        final UUID purchaseId = UUID.randomUUID();
        final PurchaseProductDTO purchaseProduct1 = new PurchaseProductDTO(1, 2,
                BigDecimal.valueOf(100.0));
        final PurchaseProductDTO purchaseProduct2 = new PurchaseProductDTO(2, 1,
                BigDecimal.valueOf(50.0));
        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(
                purchaseId,
                List.of(purchaseProduct1, purchaseProduct2));

        final Product product1 = new Product();
        product1.setProductId(1);
        product1.setName("Product 1");
        product1.setPrice(BigDecimal.valueOf(100.00));
        product1.setStockStatus(StockStatus.AVAILABLE);
        product1.setStockCount(10);

        final Product product2 = new Product();
        product2.setProductId(2);
        product2.setName("Product 2");
        product2.setPrice(BigDecimal.valueOf(50.00));
        product2.setStockStatus(StockStatus.AVAILABLE);
        product2.setStockCount(5);
        final ProductDTO productDTO1 = new ProductDTO(product1);
        final ProductDTO productDTO2 = new ProductDTO(product2);

        final RestApiResponse<List<ProductDTO>> restApiResponse = RestApiResponse.<List<ProductDTO>>builder()
                .result(List.of(productDTO1, productDTO2))
                .build();

        when(restTemplateUtil.getForObject(urlCaptor.capture(),
                responseTypeCaptor.capture())).thenReturn(restApiResponse);
        when(productPurchaseReserveRepository.findAllByProductId(1)).thenReturn(List.of());
        when(productPurchaseReserveRepository.findAllByProductId(2)).thenReturn(List.of());

        // when
        assertDoesNotThrow(() -> productsCanBePurchasedValidator.validate(enrichedPurchaseRequest));

        // then
        verify(restTemplateUtil, times(1)).getForObject(any(String.class),
                any(ParameterizedTypeReference.class));
        verify(productPurchaseReserveRepository, times(1)).findAllByProductId(1);
        verify(productPurchaseReserveRepository, times(1)).findAllByProductId(2);

        // Check captured URL
        assertEquals("http://localhost:8080/products?productIds=1&productIds=2",
                urlCaptor.getValue());
    }

    @Test
    void testValidate_ProductNotAvailable_ThrowsValidationException() throws Exception {
        // given
        final UUID purchaseId = UUID.randomUUID();
        final PurchaseProductDTO purchaseProduct = new PurchaseProductDTO(1, 15,
                BigDecimal.valueOf(100.0)); // Requesting 15 units
        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(
                purchaseId,
                List.of(purchaseProduct));

        final Product product = new Product();
        product.setProductId(1);
        product.setName("Product 1");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setStockStatus(StockStatus.AVAILABLE);
        product.setStockCount(10);

        final ProductDTO productDTO = new ProductDTO(product);

        final RestApiResponse<List<ProductDTO>> restApiResponse = RestApiResponse.<List<ProductDTO>>builder()
                .result(List.of(productDTO))
                .build();

        when(restTemplateUtil.getForObject(urlCaptor.capture(),
                responseTypeCaptor.capture())).thenReturn(restApiResponse);
        when(productPurchaseReserveRepository.findAllByProductId(1)).thenReturn(List.of());

        // when
        final ValidationException exception = assertThrows(ValidationException.class,
                () -> productsCanBePurchasedValidator.validate(enrichedPurchaseRequest));

        // then
        assertEquals("Purchase not valid as product 1 does not have sufficient stock",
                exception.getMessage());

        verify(restTemplateUtil, times(1)).getForObject(any(String.class),
                any(ParameterizedTypeReference.class));
        verify(productPurchaseReserveRepository, times(1)).findAllByProductId(1);
    }

    @Test
    void testValidate_RestTemplateThrowsException_ServiceUnavailableException() throws Exception {
        // given
        final UUID purchaseId = UUID.randomUUID();
        final PurchaseProductDTO purchaseProduct = new PurchaseProductDTO(1, 1,
                BigDecimal.valueOf(100.0));
        final EnrichedPurchaseRequest enrichedPurchaseRequest = new EnrichedPurchaseRequest(
                purchaseId,
                List.of(purchaseProduct));

        when(restTemplateUtil.getForObject(urlCaptor.capture(),
                responseTypeCaptor.capture())).thenThrow(
                new RuntimeException("Service Unavailable"));

        // when
        final Exception exception = assertThrows(Exception.class,
                () -> productsCanBePurchasedValidator.validate(enrichedPurchaseRequest));

        // then
        assertTrue(exception.getMessage().contains("Service Unavailable"));

        verify(restTemplateUtil, times(1)).getForObject(any(String.class),
                any(ParameterizedTypeReference.class));
        verify(productPurchaseReserveRepository, never()).findAllByProductId(anyInt());
    }
}