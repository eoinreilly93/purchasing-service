package com.shop.generic.purchasingservice.repositories;

import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
//@DataJpaTest
class ProductPurchaseReserveRepositoryTest {

//    @Autowired
//    private TestEntityManager testEntityManager;

    @Autowired
    private ProductPurchaseReserveRepository productPurchaseReserveRepository;

//    @BeforeEach
//    public void setUp() {
//        this.testEntityManager.flush();
//    }

    @Test
    public void saveProductPurchaseReserve() {
        final ProductPurchaseReserve productPurchaseReserve = new ProductPurchaseReserve();
        productPurchaseReserve.setPurchaseId(UUID.randomUUID());
        productPurchaseReserve.setProductId(1);
        productPurchaseReserve.setQuantity(10);

        this.productPurchaseReserveRepository.save(productPurchaseReserve);
    }
}