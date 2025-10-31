package com.fourweekdays.fourweekdays.inbound.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.inbound.exception.InboundException;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType.INBOUND_STATUS_TRANSITION_NOT_ALLOWED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Inbound extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String inboundCode;

    @Enumerated(EnumType.STRING)
    private InboundStatus status;

    // TODO: Member와 연관관계
    private String managerName; // 입고 담당자
    private String workerName; // 작업 담당자

    private LocalDateTime scheduledDate; // 입고 예정 일시
    //    private LocalDateTime receivedDate; // 실제 입고(도착) 일시
//    private LocalDateTime startedDate; // 작업 시작 일시
//    private LocalDateTime completedDate; // 작업 완료 일시
//
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @Builder.Default
    @OneToMany(mappedBy = "inbound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InboundProduct> products = new ArrayList<>();

    private String description; // 비고

    //    private String invoiceNumber; // 송장 번호
//    private String receivedBy; // 입고 담당자
//    private String driverName; // 배달 기사
//    private String driverPhoneNumber;


    // ===== ===== //
    public void updateData(String managerName, LocalDateTime scheduledDate, String description) {
        this.managerName = managerName;
        this.scheduledDate = scheduledDate;
        this.description = description;
    }

    public void updateItems(List<InboundProduct> newItems) {
        this.products.clear();
        this.products.addAll(newItems);
    }

    public void cancelInbound() {
        this.status = InboundStatus.CANCELLED;
    }

    public Optional<InboundProduct> findProductById(Long inboundProductId) {
        return products.stream()
                .filter(product -> product.getId().equals(inboundProductId))
                .findFirst();
    }

    public int getTotalReceivedQuantity() {
        return products.stream()
                .mapToInt(InboundProduct::getReceivedQuantity)
                .sum();
    }

    // ===== 입고 상태 변경 메서드 ===== //
    public void updateStatus(InboundStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            throw new InboundException(INBOUND_STATUS_TRANSITION_NOT_ALLOWED);
        }
        this.status = nextStatus;
    }

    public Long getVendorId() {
        return this.purchaseOrder.getId();
    }
}
