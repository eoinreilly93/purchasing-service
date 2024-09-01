package com.shop.generic.purchasingservice.services;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import com.shop.generic.purchasingservice.repositories.ProductPurchaseReserveRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReserveProductServiceTest {

    @Mock
    private ProductPurchaseReserveRepository productPurchaseReserveRepository;

    @InjectMocks
    private ReserveProductService reserveProductService;

    @SuppressWarnings("unchecked")
    @Test
    void testReserveProducts_Success() {
        // Given
        final UUID purchaseId = UUID.randomUUID();
        final PurchaseProductDTO productDTO1 = new PurchaseProductDTO(1, 5,
                new BigDecimal("29.99"));
        final PurchaseProductDTO productDTO2 = new PurchaseProductDTO(2, 10,
                new BigDecimal("99.99"));
        final List<PurchaseProductDTO> purchaseProductDTOS = List.of(productDTO1, productDTO2);

        // When
        reserveProductService.reserveProducts(purchaseId, purchaseProductDTOS);

        // Then
        final ArgumentCaptor<List<ProductPurchaseReserve>> captor = ArgumentCaptor.forClass(
                List.class);

        verify(productPurchaseReserveRepository, times(1)).saveAll(captor.capture());

        final List<ProductPurchaseReserve> savedReserves = captor.getValue();
        assert (savedReserves.size() == 2);

        assert (savedReserves.get(0).getPurchaseId().equals(purchaseId));
        assert (savedReserves.get(0).getProductId() == 1);
        assert (savedReserves.get(0).getQuantity() == 5);

        assert (savedReserves.get(1).getPurchaseId().equals(purchaseId));
        assert (savedReserves.get(1).getProductId() == 2);
        assert (savedReserves.get(1).getQuantity() == 10);
    }

    @Test
    void testDeleteProductReservation_Success() {
        // Given
        final UUID purchaseId = UUID.randomUUID();

        // When
        reserveProductService.deleteProductReservation(purchaseId);

        // Then
        verify(productPurchaseReserveRepository, times(1)).deleteByPurchaseId(eq(purchaseId));
    }
}
