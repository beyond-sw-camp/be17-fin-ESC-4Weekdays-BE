package com.fourweekdays.fourweekdays.purchaseorder.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @Builder.Default
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderProduct> products = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status;

    @Column(length = 1000)
    private String description;

    private LocalDateTime orderDate; // 발주일
    private LocalDateTime expectedDate; // 입고 예정일
    private Long totalAmount;
    private String rejectedReason;
    private LocalDateTime rejectedAt;


    // ===== 연관관계 편의 메서드 ===== //
    public void addItem(PurchaseOrderProduct purchaseOrderProduct) {
        this.products.add(purchaseOrderProduct);
        purchaseOrderProduct.mappingPurchaseOrder(this);
    }

    // ===== 비즈니스 로직 ===== //
    public void update(LocalDateTime expectedDate, String description) {
        if (expectedDate != null) this.expectedDate = expectedDate;
        if (description != null) this.description = description;
    }

    public void rejectByVendor(String reason) {
        this.rejectedReason = reason;
        this.rejectedAt = LocalDateTime.now();
        this.status = PurchaseOrderStatus.CANCELLED;
    }

    public Long calculateTotalAmount() {
        return products.stream()
                .mapToLong(PurchaseOrderProduct::calculateAmount)
                .sum();
    }

    private void recalculateTotalAmount() {
        this.totalAmount = calculateTotalAmount();
    }

    public void removeItem(PurchaseOrderProduct item) {
        this.products.remove(item);
        recalculateTotalAmount();
    }

    public void clearItems() {
        this.products.clear();
        this.totalAmount = 0L;
    }

    // ===== 상태 관리 메서드 ===== //
    public void approve() {
        this.status = PurchaseOrderStatus.APPROVED;
    }

    // 발주 확정 (공급사 납품 준비 완료)
    public void awaitDelivery() {
        this.status = PurchaseOrderStatus.AWAITING_DELIVERY;
    }

    // 입고 완료 처리 (ASN → 입고 완료 시점)
    public void completeDelivery() {
        this.status = PurchaseOrderStatus.COMPLETED;
    }

    // 발주 취소
    public void cancel() {
        this.status = PurchaseOrderStatus.CANCELLED;
    }

    // 상품 제거
    public void deleteItem(PurchaseOrderProduct item) {
        if (this.products.remove(item)) {
            recalculateTotalAmount();
        }
    }
}
