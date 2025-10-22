package com.fourweekdays.fourweekdays.inventory.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryException;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType.INVALID_INCREASE_QUANTITY;


@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"product_id", "location_id"})
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Inventory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private long quantity;  // 재고 수량

    @Builder
    private Inventory(Product product, Location location, Long quantity) {
        this.product = product;
        this.location = location;
        this.quantity = quantity;
    }

    // 정적 팩토리 메서드
    public static Inventory create(Product product, Location location, long quantity) {
        return Inventory.builder()
                .product(product)
                .location(location)
                .quantity(quantity)
                .build();
    }

    public void increase(long quantity) {
        if (quantity < 0) {
            throw new InventoryException(INVALID_INCREASE_QUANTITY);
        }
        this.quantity += quantity;
    }

    public void decrease(long quantity) {
        if (quantity <= 0) {
            throw new InventoryException(InventoryExceptionType.INVALID_DECREASE_QUANTITY);
        }
        if (this.quantity < quantity) {
            throw new InventoryException(InventoryExceptionType.INSUFFICIENT_INVENTORY);
        }
        this.quantity -= quantity;
    }

    // 입고/출고를 관계맺어 사용하게 된다면 Member를 지우고 입고/출고에 할당되어 있는 작업자를 조회
//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    private Member member; // 적치 작업자가 누구인지

//    @ManyToOne
//    @JoinColumn(name = "inbound_id")
//    private Inbound inbound;
//    입고가 언제 되었는지 어떤 입고 작업이었는지 기록하면 좋을 것 같음

//    @ManyToOne
//    @JoinColumn(name = "outbound_id")
//    private Outbound outbound;
//    출고는 재고 기록용으로

//    @ManyToOne
//    @JoinColumn(name = "warehouse_id")
//    private Warehouse warehouse;
}
