package com.fourweekdays.fourweekdays.search.controller.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 통합 검색 응답
 * inner_hits 결과를 포함한 검색 결과
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    // 검색 메타 정보
    private String domain;             // 검색한 도메인
    private Long totalHits;            // 전체 문서 개수
    private Integer page;
    private Integer size;

    // 검색 결과
    private List<SearchResult> results;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResult {
        
        // Vendor 정보
        private Long vendorId;
        private String vendorCode;
        private String vendorName;

        // Product 정보
        private Long productId;
        private String productCode;
        private String productName;
        private String productStatus;

        // Inner Hits - 매칭된 도메인 데이터
        private List<Map<String, Object>> matchedItems;
        private Long matchedItemsTotal;
    }
}
