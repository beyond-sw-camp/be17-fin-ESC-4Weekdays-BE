package com.fourweekdays.fourweekdays.inbound.model.entity;


import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderProduct;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InboundProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inbound_product_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_id", nullable = false)
    private Inbound inbound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_product_item_id")
    private PurchaseOrderProduct purchaseOrderProduct;

    @Column(nullable = false)
    private Integer receivedQuantity; // 입고 수량

    @Column(length = 50, nullable = true)
    private String lotNumber; // 로트번호
    private String locationCode; // 적재위치 (A-01-01) TODO: Location 엔티티 or VO 격상

//    @Column
//    private LocalDate expirationDate; // TODO: 이게 필요한가?

    @Column(length = 1000)
    private String description; // 비고

    @Builder
    public InboundProduct(Inbound inbound, Product product,
                          PurchaseOrderProduct purchaseOrderProduct,
                          Integer receivedQuantity, String lotNumber, String locationCode, String description) {
        assignInbound(inbound);
        this.product = product;
        this.purchaseOrderProduct = purchaseOrderProduct;
        this.receivedQuantity = receivedQuantity;
        this.lotNumber = lotNumber;
        this.locationCode = locationCode;
        this.description = description;
    }

    // ===== 연관관계 편의 메서드 ===== //
    public void assignInbound(Inbound inbound) {
        this.inbound = inbound;
        inbound.getProducts().add(this);
    }

    // ===== 비즈니스 로직 ===== //
    // ... 입고 수량 검증 메서드 ...
    public void updateInspectionResult(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
}
