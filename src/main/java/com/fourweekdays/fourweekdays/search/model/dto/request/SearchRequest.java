package com.fourweekdays.fourweekdays.search.model.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SearchRequest {

    private String domain; //  inbound, outbound, order, inventory, purchaseOrder

    private Long vendorId;
    private String vendorCode;
    private String vendorName;

    private Long productId;
    private String productCode;
    private String productName;
    private String productStatus;

    private String code;               // inboundCode, outboundCode, orderCode, purchaseOrderCode

    // Status 필드 (공통)
    private List<String> statuses;     // 상태 (여러 개 가능)

    private LocalDate dateFrom;        // scheduledDate, orderDate 시작
    private LocalDate dateTo;          // scheduledDate, orderDate 종료

    private String name;               // 한글 text 검색

    private Integer quantityMin;       // 최소 수량
    private Integer quantityMax;       // 최대 수량

    private String outboundType;

    private String locationCode;

    private String lotNumber;

    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;

    private Integer page;              // 0부터 시작
    private Integer size;

    private Integer innerHitsSize;     // inner_hits에서 가져올 최대 개수 (기본 100)
}
