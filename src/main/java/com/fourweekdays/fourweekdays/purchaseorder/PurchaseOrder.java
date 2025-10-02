package com.fourweekdays.fourweekdays.purchaseorder;

import com.fourweekdays.fourweekdays.franchise.FranchiseStore;
import com.fourweekdays.fourweekdays.product.Product;
import jakarta.persistence.*;

@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private FranchiseStore store; // 가맹점 / 발주 요청자

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity; // 발주 수량
    private Long amount; // 발주 금액

    @Enumerated(EnumType.STRING)
    private Status status;

}
