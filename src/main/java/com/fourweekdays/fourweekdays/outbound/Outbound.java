package com.fourweekdays.fourweekdays.outbound;

import com.fourweekdays.fourweekdays.franchise.FranchiseStore;
import com.fourweekdays.fourweekdays.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "outbound")
public class Outbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outboundId; // 출고 ID

    private int quantity;

    @Enumerated(EnumType.STRING)
    private OutboundType outboundType;

//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Order order;
//
//    @ManyToOne
//    @JoinColumn(name = "franchise_store_id")
//    private FranchiseStore store;
//
//    @ManyToOne
//    @JoinColumn(name = "product_id")
//    private Product product; // 출고 상품
}