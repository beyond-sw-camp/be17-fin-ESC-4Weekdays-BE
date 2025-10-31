package com.fourweekdays.fourweekdays.purchaseorder.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PurchaseOrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer orderedQuantity; // 발주 수량

    @Column(length = 500)
    private String description; // 비고


    // ===== 연관관계 편의 메서드 ===== //
    public void mappingPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    // ===== 비즈니스 로직 ===== //
    public Long calculateAmount() {
        return this.product.getUnitPrice() * orderedQuantity;
    }

    // ... 입고 진행률 조회 메서드 (Inbound에서 집계) ...
}

