package com.fourweekdays.fourweekdays.outbound.model.entity;

import com.fourweekdays.fourweekdays.order.model.entity.OrderProductItem;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OutboundProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_id", nullable = false)
    private Outbound outbound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_item_id")
    private OrderProductItem orderProductItem;

    @Column(nullable = false)
    private Integer orderedQuantity; // 주문 수량

//    @Column(length = 50, nullable = true)
//    private String lotNumber; // 로트번호 이거 상품에 대해 개별적인 추적이 필요할 때만 사용한다고 하네요 우리는 화장품이라 필요 없는것 같아요
    private String locationCode; // 적재위치 (A-01-01) TODO: Location 엔티티 or VO 격상

    @Column(length = 1000)
    private String description; // 비고

    // ===== 연관관계 편의 메서드 ===== //
    public void assignOutbound(Outbound outbound) {
        this.outbound = outbound;
        outbound.getItems().add(this);
    }

    // ===== 비즈니스 로직 ===== //
    public void updateInspectionResult(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }
}
