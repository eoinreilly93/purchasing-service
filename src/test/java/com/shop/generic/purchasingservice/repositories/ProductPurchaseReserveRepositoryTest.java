package com.shop.generic.purchasingservice.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductPurchaseReserveRepositoryTest {

    @Autowired
    private ProductPurchaseReserveRepository productPurchaseReserveRepository;

    @Test
    void testFindAllByProductId() {
        // Arrange
        final int productId = 1;
        final ProductPurchaseReserve reserve1 = new ProductPurchaseReserve();
        reserve1.setProductId(productId);
        reserve1.setPurchaseId(UUID.randomUUID());
        reserve1.setQuantity(10);

        final ProductPurchaseReserve reserve2 = new ProductPurchaseReserve();
        reserve2.setProductId(productId);
        reserve2.setPurchaseId(UUID.randomUUID());
        reserve2.setQuantity(5);

        productPurchaseReserveRepository.save(reserve1);
        productPurchaseReserveRepository.save(reserve2);

        // Act
        final List<ProductPurchaseReserve> foundReserves = productPurchaseReserveRepository.findAllByProductId(
                productId);

        // Assert
        assertNotNull(foundReserves);
        assertEquals(2, foundReserves.size());
        assertTrue(foundReserves.stream().anyMatch(reserve -> reserve.equals(reserve1)));
        assertTrue(foundReserves.stream().anyMatch(reserve -> reserve.equals(reserve2)));
    }

    @Test
    void testDeleteByPurchaseId() {
        // Arrange
        final UUID purchaseId = UUID.randomUUID();
        final ProductPurchaseReserve reserve = new ProductPurchaseReserve();
        reserve.setProductId(1);
        reserve.setPurchaseId(purchaseId);
        reserve.setQuantity(10);

        productPurchaseReserveRepository.save(reserve);

        // Act
        productPurchaseReserveRepository.deleteByPurchaseId(purchaseId);

        // Assert
        final Optional<ProductPurchaseReserve> foundReserve = productPurchaseReserveRepository.findByPurchaseId(
                reserve.getPurchaseId());
        assertFalse(foundReserve.isPresent());
    }

    @Test
    void testFindAllByProductId_NoResults() {
        // Act
        final List<ProductPurchaseReserve> foundReserves = productPurchaseReserveRepository.findAllByProductId(
                999);

        // Assert
        assertNotNull(foundReserves);
        assertTrue(foundReserves.isEmpty());
    }
}
