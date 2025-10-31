package com.fourweekdays.fourweekdays.inventory.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryException;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType.INSUFFICIENT_INVENTORY;
import static com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType.INVALID_QUANTITY;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Inventory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inbound_id", insertable = false, updatable = false)
    private Inbound inbound;

//    @Column(name = "inbound_id", insertable = false, updatable = false)
//    private Long inboundId; // 입고 history 추적 때문에 넣음

    @Column(length = 50)
    private String lotNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 1000)
    private String description;

    @Builder
    public Inventory(Product product, Location location, String lotNumber,
                     Integer quantity, String description) {
        this.product = product;
        this.location = location;
        this.lotNumber = lotNumber;
        this.quantity = quantity;
        this.description = description;
    }

    // ===== 비즈니스 로직 ===== //
    public void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new InventoryException(INVALID_QUANTITY);
        }
        this.location.increaseUsedCapacity(amount); // 용량 체크 및 증가
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new InventoryException(INVALID_QUANTITY);
        }
        if (this.quantity < amount) {
            throw new InventoryException(INSUFFICIENT_INVENTORY);
        }
        this.quantity -= amount;
        this.location.decreaseUsedCapacity(amount);
    }
}