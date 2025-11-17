package com.fourweekdays.fourweekdays.search.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class SearchResponse {

    private String domain;
    private Long totalHits;
    private Integer page;
    private Integer size;

    // 검색 결과
    private List<SearchResult> results;

    @Getter
    @Builder
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
