package com.fourweekdays.fourweekdays.common;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {

    /**
     * 20000 : 요청 성공
     */
    SUCCESS(true, 20000, "요청에 성공하였습니다."),
    SUCCESS_UPDATE(true, 20001, "수정에 성공하였습니다."),

    // ✅ [Task 관련 성공 응답]
    TASK_CREATED(true, 20010, "작업이 생성되었습니다."),
    TASK_STATUS_UPDATED(true, 20011, "작업 상태가 변경되었습니다."),

    /**
     * 30000 : Request 오류
     */


    /**
     * 40000 : Response 오류
     */
    NOT_FOUND(false, 40000, "찾을 수 없는 리소스입니다."),
    PRODUCT_NOT_FOUND(false, 40001, "해당 상품을 찾을 수 없습니다."),
    INVALID_TOKEN(false, 40002, "유효하지 않은 토큰입니다."),

    // ✅ [Outbound 관련 오류]
    OUTBOUND_NOT_FOUND(false, 40010, "해당 출고서를 찾을 수 없습니다."),
    OUTBOUND_ALREADY_APPROVED(false, 40011, "이미 승인된 출고서입니다."),
    OUTBOUND_ALREADY_REJECTED(false, 40012, "이미 거절된 출고서입니다."),
    OUTBOUND_INVALID_STATUS(false, 40013, "출고서 상태가 유효하지 않습니다."),
    INBOUND_NOT_FOUND(false, 40014, "해당 입고서를 찾을 수 없습니다."),
    WORKER_NOT_FOUND(false, 40015, "해당 작업자를 찾을 수 없습니다."),
    TASK_NOT_FOUND(false, 40016, "해당 작업을 찾을 수 없습니다."),
    TASK_ALREADY_ASSIGNED(false, 40017, "이미 담당자가 할당된 작업입니다."),
    TASK_ALREADY_IN_PROGRESS(false, 40018, "이미 진행 중인 작업이 존재합니다."),

    /**
     * 50000 : Database, Server 오류
     */
    SERVER_ERROR(false, 50000, "서버 내부 오류가 발생했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
