package com.fourweekdays.fourweekdays.search.controller.controller;

import com.fourweekdays.fourweekdays.search.controller.model.dto.request.SearchRequest;
import com.fourweekdays.fourweekdays.search.controller.model.dto.response.SearchResponse;
import com.fourweekdays.fourweekdays.search.controller.service.UnifiedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 통합 검색 API
 * 
 * 사용 예시:
 * - Inbound 검색: GET /api/unified-search?domain=inbound&statuses=CREATED&dateFrom=2024-01-01
 * - Order 검색: GET /api/unified-search?domain=order&name=강남점&statuses=SHIPPED
 * - POST 방식: POST /api/unified-search (복잡한 조건)
 */
@RestController
@RequestMapping("/api/unified-search")
@RequiredArgsConstructor
public class SearchController {

    private final UnifiedSearchService unifiedSearchService;

    /**
     * 통합 검색 (GET 방식)
     * 
     * 예시:
     * GET /api/unified-search?domain=order&vendorName=아모레&productName=립스틱&statuses=SHIPPED&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<SearchResponse> search(
            // 필수: 도메인 선택
            @RequestParam String domain,
            
            // Vendor 조건
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String vendorCode,
            @RequestParam(required = false) String vendorCodePrefix,
            @RequestParam(required = false) String vendorName,
            
            // Product 조건
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productStatus,
            
            // Domain 공통 조건
            @RequestParam(required = false) String code,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer quantityMin,
            @RequestParam(required = false) Integer quantityMax,
            
            // Domain 특수 조건
            @RequestParam(required = false) String outboundType,
            @RequestParam(required = false) String locationCode,
            @RequestParam(required = false) String lotNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,
            
            // 페이징
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer innerHitsSize
    ) {
        SearchRequest condition = SearchRequest.builder()
            .domain(domain)
            .vendorId(vendorId)
            .vendorCode(vendorCode)
            .vendorCodePrefix(vendorCodePrefix)
            .vendorName(vendorName)
            .productId(productId)
            .productCode(productCode)
            .productName(productName)
            .productStatus(productStatus)
            .code(code)
            .statuses(statuses)
            .dateFrom(dateFrom)
            .dateTo(dateTo)
            .name(name)
            .quantityMin(quantityMin)
            .quantityMax(quantityMax)
            .outboundType(outboundType)
            .locationCode(locationCode)
            .lotNumber(lotNumber)
            .dueDateFrom(dueDateFrom)
            .dueDateTo(dueDateTo)
            .page(page)
            .size(size)
            .innerHitsSize(innerHitsSize)
            .build();

        SearchResponse response = unifiedSearchService.search(condition);
        return ResponseEntity.ok(response);
    }

    /**
     * 통합 검색 (POST 방식)
     * 
     * 복잡한 조건이나 많은 파라미터가 필요한 경우 사용
     * 
     * 예시:
     * POST /api/unified-search
     * {
     *   "domain": "order",
     *   "vendorName": "아모레퍼시픽",
     *   "productName": "립스틱",
     *   "statuses": ["SHIPPED", "DELIVERED"],
     *   "dateFrom": "2024-01-01",
     *   "dateTo": "2024-12-31",
     *   "page": 0,
     *   "size": 20
     * }
     */
    @PostMapping
    public ResponseEntity<SearchResponse> searchPost(
            @RequestBody SearchRequest condition
    ) {
        SearchResponse response = unifiedSearchService.search(condition);
        return ResponseEntity.ok(response);
    }

    /**
     * 검색 결과 개수 조회
     * 
     * GET /api/unified-search/count?domain=order&statuses=SHIPPED
     */
    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam String domain,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(required = false) String vendorCode,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) List<String> statuses
    ) {
        SearchRequest condition = SearchRequest.builder()
            .domain(domain)
            .vendorId(vendorId)
            .vendorCode(vendorCode)
            .vendorName(vendorName)
            .productName(productName)
            .statuses(statuses)
            .build();

        long count = unifiedSearchService.count(condition);
        return ResponseEntity.ok(count);
    }
}
