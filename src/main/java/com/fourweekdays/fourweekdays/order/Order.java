package com.fourweekdays.fourweekdays.order;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.franchise.FranchiseStore;
import com.fourweekdays.fourweekdays.product.Product;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // 주문 ID

    private LocalDateTime dueDate;   // 납기일
    private int quantity; // 주문 수량
    private Long amount; // 주문 금액

    @ManyToOne
    @JoinColumn(name = "franchise_store_id")
    private FranchiseStore franchiseStore;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 주문 상품

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태
}
