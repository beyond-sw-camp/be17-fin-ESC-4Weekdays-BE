package com.fourweekdays.fourweekdays.tasks;

import com.fourweekdays.fourweekdays.Inbound.Inbound;
import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.inventory.Inventory;
import com.fourweekdays.fourweekdays.member.Member;
import com.fourweekdays.fourweekdays.outbound.Outbound;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 작업 ID (PK)

    @Enumerated(EnumType.STRING)
    private TaskType type; // 작업 유형 (입고 / 출고 / 재고조사 등)

    @Enumerated(EnumType.STRING)
    private Status status; // 작업 상태 (할당됨 / 진행중 / 완료 / 취소)

    private String description; // 작업 설명 (예: "A상품 100개 입고 확인")

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private Member assignedTo; // 작업 담당자

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Member createdBy; // 작업 지시자 (관리자)

    // 관련 업무와 연결
    @ManyToOne
    @JoinColumn(name = "inbound_id")
    private Inbound inbound; // 입고 관련 작업

    @ManyToOne
    @JoinColumn(name = "outbound_id")
    private Outbound outbound; // 출고 관련 작업

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory; // 재고 관련 작업

    private LocalDateTime startedAt;   // 작업 시작일시
    private LocalDateTime completedAt; // 작업 완료일시

}
