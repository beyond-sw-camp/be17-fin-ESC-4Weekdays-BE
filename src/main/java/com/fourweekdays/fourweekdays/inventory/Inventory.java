package com.fourweekdays.fourweekdays.inventory;

import com.fourweekdays.fourweekdays.product.Product;
import jakarta.persistence.*;

@Entity
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId; // 재고 ID

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product; // 어떤 상품인지

    private int quantity;  // 재고 수량
    private String location; // 보관 위치 (선반, 랙 등 예: A12309 구역)

//    @ManyToOne
//    @JoinColumn(name = "warehouse_id")
//    private Warehouse warehouse;
}
