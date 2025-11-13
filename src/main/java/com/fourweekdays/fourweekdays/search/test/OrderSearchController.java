package com.fourweekdays.fourweekdays.search.test;

import com.fourweekdays.fourweekdays.search.ProductSearchDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 주문 검색 API
 * 상품 기준으로 주문을 검색
 */
@RestController
@RequestMapping("/api/order-search")
@RequiredArgsConstructor
public class OrderSearchController {

    private final OrderSearchService orderSearchService;

    /**
     * 주문 검색 (동적 쿼리)
     *
     * 예시:
     * GET /api/order-search?productName=김치&orderStatuses=CONFIRMED,PENDING&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String productStatus,
            @RequestParam(required = false) String orderCode,
            @RequestParam(required = false) List<String> orderStatuses,
            @RequestParam(required = false) String franchiseName,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorCode,
            @RequestParam(required = false) Long vendorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        OrderSearchCondition condition = OrderSearchCondition.builder()
                .productName(productName)
                .productCode(productCode)
                .productId(productId)
                .productStatus(productStatus)
                .orderCode(orderCode)
                .orderStatuses(orderStatuses)
                .franchiseName(franchiseName)
                .vendorName(vendorName)
                .vendorCode(vendorCode)
                .vendorId(vendorId)
                .page(page)
                .size(size)
                .build();

        SearchHits<ProductSearchDocument> searchHits = orderSearchService.searchOrders(condition);

        Map<String, Object> response = new HashMap<>();
        response.put("content", searchHits.getSearchHits());
        response.put("totalHits", searchHits.getTotalHits());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 검색 (POST 방식 - 복잡한 조건)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> searchOrdersPost(
            @RequestBody OrderSearchCondition condition) {

        SearchHits<ProductSearchDocument> searchHits = orderSearchService.searchOrders(condition);

        Map<String, Object> response = new HashMap<>();
        response.put("content", searchHits.getSearchHits());
        response.put("totalHits", searchHits.getTotalHits());
        response.put("page", condition.getPage() != null ? condition.getPage() : 0);
        response.put("size", condition.getSize() != null ? condition.getSize() : 20);

        return ResponseEntity.ok(response);
    }

    /**
     * 간단한 리스트 반환 (페이징 정보 제외)
     */
    @GetMapping("/list")
    public ResponseEntity<List<ProductSearchDocument>> searchOrdersList(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) List<String> orderStatuses,
            @RequestParam(required = false) String franchiseName,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        OrderSearchCondition condition = OrderSearchCondition.builder()
                .productName(productName)
                .orderStatuses(orderStatuses)
                .franchiseName(franchiseName)
                .page(page)
                .size(size)
                .build();

        List<ProductSearchDocument> results = orderSearchService.searchOrdersAsList(condition);

        return ResponseEntity.ok(results);
    }
}
