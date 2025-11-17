package com.fourweekdays.fourweekdays.search.service;

import com.fourweekdays.fourweekdays.search.model.entity.ProductSearchDocument;
import com.fourweekdays.fourweekdays.search.model.dto.request.SearchRequest;
import com.fourweekdays.fourweekdays.search.repository.SearchRepository;
import com.fourweekdays.fourweekdays.search.model.dto.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchRepository unifiedSearchRepository;

    public SearchResponse search(SearchRequest condition) {
        log.info("통합 검색 시작 - 도메인: {}", condition.getDomain());

        // 1. Elasticsearch 검색
        SearchHits<ProductSearchDocument> searchHits =
            unifiedSearchRepository.unifiedSearch(condition);

        // 2. Inner Hits 파싱
        List<SearchResponse.SearchResult> results = parseSearchHits(
            searchHits, condition.getDomain());

        // 3. 응답 생성
        SearchResponse response = SearchResponse.builder()
            .domain(condition.getDomain())
            .totalHits(searchHits.getTotalHits())
            .page(condition.getPage() != null ? condition.getPage() : 0)
            .size(condition.getSize() != null ? condition.getSize() : 20)
            .results(results)
            .build();

        log.info("통합 검색 완료 - 도메인: {}, 결과: {} 건",
            condition.getDomain(), searchHits.getTotalHits());

        return response;
    }

    private List<SearchResponse.SearchResult> parseSearchHits(
        SearchHits<ProductSearchDocument> searchHits,
        String domain
    ) {
        List<SearchResponse.SearchResult> results = new ArrayList<>();
        String innerHitsName = "matched_" + domain.toLowerCase();

        for (SearchHit<ProductSearchDocument> hit : searchHits) {
            ProductSearchDocument doc = hit.getContent();

            // Inner Hits 추출
            Map<String, SearchHits<?>> innerHitsMap = hit.getInnerHits();
            SearchHits<?> innerHits = innerHitsMap.get(innerHitsName);

            if (innerHits == null || innerHits.isEmpty()) {
                continue; // Inner Hits가 없으면 스킵
            }

            // Inner Hits 데이터 변환
            List<Map<String, Object>> matchedItems = innerHits.stream()
                .map(innerHit -> (Map<String, Object>) innerHit.getContent())
                .collect(Collectors.toList());

            // Product 정보 추출 (첫 번째 product만 - 실제로는 inner hits의 parent)
            ProductSearchDocument.ProductInfo productInfo =
                doc.getProducts() != null && !doc.getProducts().isEmpty()
                    ? doc.getProducts().get(0)
                    : null;

            SearchResponse.SearchResult result = SearchResponse.SearchResult.builder()
                .vendorId(doc.getVendorId())
                .vendorCode(doc.getVendorCode())
                .vendorName(doc.getVendorName())
                .productId(productInfo != null ? productInfo.getProductId() : null)
                .productCode(productInfo != null ? productInfo.getProductCode() : null)
                .productName(productInfo != null ? productInfo.getProductName() : null)
                .productStatus(productInfo != null ? productInfo.getProductStatus() : null)
                .matchedItems(matchedItems)
                .matchedItemsTotal(innerHits.getTotalHits())
                .build();

            results.add(result);
        }

        return results;
    }

}
