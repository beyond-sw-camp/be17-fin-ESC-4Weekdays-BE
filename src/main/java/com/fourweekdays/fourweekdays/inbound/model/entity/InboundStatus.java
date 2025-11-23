package com.fourweekdays.fourweekdays.inbound.model.entity;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum InboundStatus {

    CREATED("입고서 생성"), // 발주 or 입고지시 생성 상태
    SCHEDULED("입고 예정"), // 실제 도착 일정이 잡힘
    ARRIVED("도착"), // 차량 도착 or ASN 접수 완료
    INSPECTING("검수중"), // 수량/품질 검수 단계
    PUTAWAY("적치중"), // 창고 위치로 이동 중
    COMPLETED("입고 완료"), // 모든 검수·적치 완료
    CANCELLED("취소");

    private final String description;

    InboundStatus(String description) {
        this.description = description;
    }

    public boolean canTransitionTo(InboundStatus next) {
        return switch (this) {
            case CREATED -> next == SCHEDULED || next == CANCELLED;
            case SCHEDULED -> next == ARRIVED || next == CANCELLED;
            case ARRIVED -> next == INSPECTING || next == CANCELLED;
            case INSPECTING -> next == PUTAWAY || next == CANCELLED;
            case PUTAWAY -> next == COMPLETED || next == CANCELLED;
            default -> false; // COMPLETED, CANCELLED 변경 불가
        };
    }
}