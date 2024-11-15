package com.shop.generic.purchasingservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "product_purchase_reserve")
@Data
public class ProductPurchaseReserve {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservation_id_gen")
    @SequenceGenerator(name = "reservation_id_gen", sequenceName = "reservation_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "purchase_id", nullable = false)
    private UUID purchaseId;

    @Column(name = "product_id", nullable = false)
    private int productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

}