package com.fourweekdays.fourweekdays.search.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCondition {

    // 상품 관련 검색 조건
    private String productName;           // 상품명 (한글 검색)
    private String productCode;           // 상품 코드
    private Long productId;               // 상품 ID
    private String productStatus;         // 상품 상태

    // 주문 관련 검색 조건
    private String orderCode;             // 주문 코드
    private List<String> orderStatuses;   // 주문 상태 (여러개 가능)
    private LocalDate orderDateFrom;      // 주문일 시작
    private LocalDate orderDateTo;        // 주문일 종료
    private LocalDate dueDateFrom;        // 납기일 시작
    private LocalDate dueDateTo;          // 납기일 종료

    // 가맹점 관련 검색 조건
    private String franchiseName;         // 가맹점명 (한글 검색)

    // 수량 관련 검색 조건
    private Integer quantityMin;          // 최소 수량
    private Integer quantityMax;          // 최대 수량

    // 벤더 관련 검색 조건
    private String vendorName;            // 벤더명 (한글 검색)
    private String vendorCode;            // 벤더 코드
    private Long vendorId;                // 벤더 ID

    // 페이징
    private Integer page;                 // 페이지 번호 (0부터 시작)
    private Integer size;                 // 페이지 크기
}