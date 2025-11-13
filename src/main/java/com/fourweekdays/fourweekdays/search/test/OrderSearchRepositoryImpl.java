package com.fourweekdays.fourweekdays.search.test;

import com.fourweekdays.fourweekdays.search.ProductSearchDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 검색 커스텀 Repository 구현체 (Criteria API 사용)
 * 더 안정적이고 간단한 API
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderSearchRepositoryImpl implements OrderSearchRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00");

    @Override
    public SearchHits<ProductSearchDocument> searchOrders(OrderSearchCondition condition) {
        Pageable pageable = createPageable(condition);
        Criteria criteria = buildOrderCriteria(condition);
        Query query = new CriteriaQuery(criteria).setPageable(pageable);
        
        SearchHits<ProductSearchDocument> searchHits = 
                elasticsearchOperations.search(query, ProductSearchDocument.class);
        
        log.debug("주문 검색 쿼리 실행 - 조건: {}, 결과: {} 건", condition, searchHits.getTotalHits());
        
        return searchHits;
    }

    /**
     * Criteria로 동적 쿼리 생성
     */
    private Criteria buildOrderCriteria(OrderSearchCondition condition) {
        List<Criteria> criteriaList = new ArrayList<>();
        
        // 벤더 조건
        if (condition.getVendorId() != null) {
            criteriaList.add(Criteria.where("vendorId").is(condition.getVendorId()));
        }
        if (StringUtils.hasText(condition.getVendorCode())) {
            criteriaList.add(Criteria.where("vendorCode").is(condition.getVendorCode()));
        }
        if (StringUtils.hasText(condition.getVendorName())) {
            criteriaList.add(Criteria.where("vendorName").matches(condition.getVendorName()));
        }
        
        // 상품 조건
        if (condition.getProductId() != null) {
            criteriaList.add(Criteria.where("products.productId").is(condition.getProductId()));
        }
        if (StringUtils.hasText(condition.getProductCode())) {
            criteriaList.add(Criteria.where("products.productCode").is(condition.getProductCode()));
        }
        if (StringUtils.hasText(condition.getProductName())) {
            criteriaList.add(Criteria.where("products.productName").matches(condition.getProductName()));
        }
        if (StringUtils.hasText(condition.getProductStatus())) {
            criteriaList.add(Criteria.where("products.productStatus").is(condition.getProductStatus()));
        }
        
        // 주문 조건
        if (StringUtils.hasText(condition.getOrderCode())) {
            criteriaList.add(Criteria.where("products.order.orderCode").is(condition.getOrderCode()));
        }
        if (condition.getOrderStatuses() != null && !condition.getOrderStatuses().isEmpty()) {
            criteriaList.add(Criteria.where("products.order.status").in(condition.getOrderStatuses()));
        }
        if (condition.getOrderDateFrom() != null) {
            criteriaList.add(Criteria.where("products.order.orderDate")
                .greaterThanEqual(condition.getOrderDateFrom().format(DATE_FORMATTER)));
        }
        if (condition.getOrderDateTo() != null) {
            criteriaList.add(Criteria.where("products.order.orderDate")
                .lessThanEqual(condition.getOrderDateTo().format(DATE_FORMATTER)));
        }
        if (condition.getDueDateFrom() != null) {
            criteriaList.add(Criteria.where("products.order.dueDate")
                .greaterThanEqual(condition.getDueDateFrom().format(DATE_FORMATTER)));
        }
        if (condition.getDueDateTo() != null) {
            criteriaList.add(Criteria.where("products.order.dueDate")
                .lessThanEqual(condition.getDueDateTo().format(DATE_FORMATTER)));
        }
        if (StringUtils.hasText(condition.getFranchiseName())) {
            criteriaList.add(Criteria.where("products.order.franchiseName")
                .matches(condition.getFranchiseName()));
        }
        if (condition.getQuantityMin() != null) {
            criteriaList.add(Criteria.where("products.order.quantity")
                .greaterThanEqual(condition.getQuantityMin()));
        }
        if (condition.getQuantityMax() != null) {
            criteriaList.add(Criteria.where("products.order.quantity")
                .lessThanEqual(condition.getQuantityMax()));
        }
        
        // 모든 조건을 AND로 결합
        if (criteriaList.isEmpty()) {
            return new Criteria();
        }
        
        Criteria finalCriteria = criteriaList.get(0);
        for (int i = 1; i < criteriaList.size(); i++) {
            finalCriteria = finalCriteria.and(criteriaList.get(i));
        }
        
        return finalCriteria;
    }

    private Pageable createPageable(OrderSearchCondition condition) {
        int page = condition.getPage() != null ? condition.getPage() : 0;
        int size = condition.getSize() != null ? condition.getSize() : 20;
        return PageRequest.of(page, size);
    }
}
