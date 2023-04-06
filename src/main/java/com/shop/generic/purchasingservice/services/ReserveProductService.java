package com.shop.generic.purchasingservice.services;

import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.purchasingservice.entities.ProductPurchaseReserve;
import com.shop.generic.purchasingservice.repositories.ProductPurchaseReserveRepository;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class ReserveProductService {

    private final ProductPurchaseReserveRepository productPurchaseReserveRepository;

    public ReserveProductService(
            final ProductPurchaseReserveRepository productPurchaseReserveRepository) {
        this.productPurchaseReserveRepository = productPurchaseReserveRepository;
    }

    public void reserveProducts(final UUID id, final List<PurchaseProductDTO> purchaseProductDTOS) {
        this.productPurchaseReserveRepository.saveAll(purchaseProductDTOS.stream().map(vo -> {
            final ProductPurchaseReserve productReserve = new ProductPurchaseReserve();
            productReserve.setPurchaseId(id);
            productReserve.setProductId(vo.productId());
            productReserve.setQuantity(vo.quantity());
            return productReserve;
        }).toList());
    }

    public void deleteProductReservation(final UUID id) {
        log.info("Deleting product reservation with it {}", id);
        this.productPurchaseReserveRepository.deleteByPurchaseId(id);
    }
}
