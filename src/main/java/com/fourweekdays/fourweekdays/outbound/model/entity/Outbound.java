package com.fourweekdays.fourweekdays.outbound.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.outbound.exception.OutboundException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fourweekdays.fourweekdays.outbound.exception.OutboundExceptionType.OUTBOUND_STATUS_TRANSITION_NOT_ALLOWED;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbound")
public class Outbound extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbound_id")
    private Long id; // 출고 ID

    @Column(nullable = false)
    private String outboundCode;

    @Enumerated(EnumType.STRING)
    private OutboundType outboundType; // 출고유형

    @Enumerated(EnumType.STRING)
    private OutboundStatus status; // 출고상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order; // 주문 기반 출고 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member outboundManager; // 출고 등록인

    private LocalDateTime scheduledDate; // 출고 예상 일시

    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "outbound", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutboundProductItem> items = new ArrayList<>();

    // ===== 입고 상태 변경 메서드 ===== //
    public void updateStatus(OutboundStatus nextStatus) {
        if (!this.status.canTransitionTo(nextStatus)) {
            throw new OutboundException(OUTBOUND_STATUS_TRANSITION_NOT_ALLOWED);
        }
        this.status = nextStatus;
    }

    public Optional<OutboundProductItem> findByItemId(Long outboundProductid) {
        return items.stream()
                .filter(item -> item.getId().equals(outboundProductid))
                .findFirst();
    }
//
//    @ManyToOne
//    @JoinColumn(name = "franchise_store_id")
//    private FranchiseStore franchiseStore;
//    Order 기반 출고를 할 경우 Order와 매핑되어 있을거니까 Franchise_store 받아올수 있고
//    Order 기반이 아니라면 아예 핑요 없는 경우가 있음
//    반대로 필요한 경우 Order 없이 franchise로 출고가 발생할 경우
//    -> 관리자의 상품기반 등록이 필요해짐 + 중복된 Order에 대해서 허용이 가능해야 함
}