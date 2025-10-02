package com.fourweekdays.fourweekdays.notification;

import com.fourweekdays.fourweekdays.Inbound.Inbound;
import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.inventory.Inventory;
import com.fourweekdays.fourweekdays.member.Member;
import com.fourweekdays.fourweekdays.outbound.Outbound;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String message;

    @Enumerated(EnumType.STRING)
    private Status status; // 알림 상태 - [읽음, 안읽음]
    private LocalDateTime readAt;    // 알림 읽은 시각 (null이면 아직 안 읽음)

    // 입고, 출고, 재고 이벤트와 연관 (nullable 가능)
    @ManyToOne
    @JoinColumn(name = "inbound_id")
    private Inbound inbound;

    @ManyToOne
    @JoinColumn(name = "outbound_id")
    private Outbound outbound;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
}
