package com.shop.generic.purchasingservice.repositories;

import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPurchaseReserveRepository extends
        JpaRepository<ProductPurchaseReserve, Integer> {

    List<ProductPurchaseReserve> findAllByProductId(Integer productId);

    void deleteByPurchaseId(UUID id);

    Optional<ProductPurchaseReserve> findByPurchaseId(UUID id);
}
