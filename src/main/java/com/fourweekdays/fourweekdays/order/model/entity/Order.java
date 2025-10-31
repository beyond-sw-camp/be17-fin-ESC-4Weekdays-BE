package com.fourweekdays.fourweekdays.order.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // 주문 ID

    @Column
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "franchise_store_id")
    private FranchiseStore franchiseStore;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductItem> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime orderDate; // 주문일

    @Column(nullable = false)
    private LocalDateTime dueDate;   // 납기일
//
//    @Column(nullable = false)
//    private Long totalAmount; // 총 금액

    @Column(length = 1000)
    private String description; // 비고

    private String rejectedReason; // 거절 사유

    private LocalDateTime rejectedAt; //취소 시간

    // ===== 연관관계 편의 메서드 ===== //
    public void addItem(OrderProductItem orderProductItem) {
        this.items.add(orderProductItem);
        orderProductItem.mappingOrder(this);
    }

    // ===== 비즈니스 메서드 ===== //
    public void rejectByFranchise(String reason) {
        this.rejectedReason = reason;
        this.rejectedAt = LocalDateTime.now();
        this.status = OrderStatus.CANCELLED;
    }

    // ===== 비즈니스 로직 ===== //
    public void updateShipped() {
        this.status = OrderStatus.SHIPPED;
    }
}
