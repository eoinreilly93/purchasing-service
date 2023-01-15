package com.shop.generic.purchasingservice.repositories;

import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPurchaseReserveRepository extends
        JpaRepository<ProductPurchaseReserve, Integer> {

}
