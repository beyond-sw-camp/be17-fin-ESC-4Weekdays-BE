package com.fourweekdays.fourweekdays.search.test;

import com.fourweekdays.fourweekdays.search.ProductSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private final OrderSearchRepository orderSearchRepository;

    public SearchHits<ProductSearchDocument> searchOrders(OrderSearchCondition condition) {
        log.info("주문 검색 요청 - 조건: {}", condition);

        SearchHits<ProductSearchDocument> searchHits = orderSearchRepository.searchOrders(condition);

        log.info("주문 검색 완료 - 결과: {} 건", searchHits.getTotalHits());

        return searchHits;
    }

    /**
     * 검색 결과를 List로 변환
     */
    public List<ProductSearchDocument> searchOrdersAsList(OrderSearchCondition condition) {
        SearchHits<ProductSearchDocument> searchHits = searchOrders(condition);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * 주문 개수 조회
     */
    public long countOrders(OrderSearchCondition condition) {
        SearchHits<ProductSearchDocument> searchHits = orderSearchRepository.searchOrders(condition);
        return searchHits.getTotalHits();
    }
}