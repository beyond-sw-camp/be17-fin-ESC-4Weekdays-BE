package com.fourweekdays.fourweekdays.search.controller.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 통합 검색 조건
 * 모든 도메인(inbound, outbound, order, inventory, purchaseOrder)을 지원
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    // ========================================
    // 검색 대상 도메인 선택 (필수)
    // ========================================
    /**
     * 검색할 도메인
     * 가능한 값: inbound, outbound, order, inventory, purchaseOrder
     */
    private String domain;

    // ========================================
    // 공통 검색 조건 (Vendor)
    // ========================================
    private Long vendorId;
    private String vendorCode;         // keyword 검색
    private String vendorCodePrefix;   // prefix 검색 (v-*)
    private String vendorName;         // 한글 text 검색

    // ========================================
    // 공통 검색 조건 (Product)
    // ========================================
    private Long productId;
    private String productCode;        // keyword 검색
    private String productName;        // 한글 text 검색
    private String productStatus;      // keyword 검색

    // ========================================
    // 도메인별 검색 조건
    // ========================================
    
    // Code 필드 (공통)
    private String code;               // inboundCode, outboundCode, orderCode, purchaseOrderCode
    
    // Status 필드 (공통)
    private List<String> statuses;     // 상태 (여러 개 가능)
    
    // Date 필드 (공통 - 기간 검색)
    private LocalDate dateFrom;        // scheduledDate, orderDate 시작
    private LocalDate dateTo;          // scheduledDate, orderDate 종료
    
    // Name 필드 (managerName, franchiseName)
    private String name;               // 한글 text 검색
    
    // Quantity 필드 (공통)
    private Integer quantityMin;       // 최소 수량
    private Integer quantityMax;       // 최대 수량
    
    // OutboundType (outbound 전용)
    private String outboundType;
    
    // LocationCode (inbound, outbound, inventory 전용)
    private String locationCode;
    
    // LotNumber (inbound, inventory 전용)
    private String lotNumber;
    
    // DueDate (order 전용)
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;

    // ========================================
    // 페이징
    // ========================================
    private Integer page;              // 0부터 시작
    private Integer size;              // 기본 20

    // ========================================
    // Inner Hits 설정
    // ========================================
    private Integer innerHitsSize;     // inner_hits에서 가져올 최대 개수 (기본 100)
}
