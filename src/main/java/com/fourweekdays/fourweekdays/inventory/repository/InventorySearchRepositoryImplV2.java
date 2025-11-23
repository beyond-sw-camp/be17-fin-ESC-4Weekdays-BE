package com.fourweekdays.fourweekdays.inventory.repository;

import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchRequest;
import com.fourweekdays.fourweekdays.inventory.model.entity.InventoryDocument;
import com.fourweekdays.fourweekdays.product.model.entity.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class InventorySearchRepositoryImplV2 implements InventorySearchRepository {

    private final ElasticsearchOperations operations;

    /**
     * 1단계: Product ES에서 검색 & Product ID 조회 (페이징)
     */
    @Override
    public List<Long> searchProductIds(InventorySearchRequest searchRequest, Pageable pageable) {
        Criteria productCriteria = buildProductCriteria(searchRequest);

        // ES 필드는 product_id 이지만, Criteria 에서는 엔티티 필드명인 "productId" 사용
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.asc("product_id"))
        );

        CriteriaQuery productQuery = new CriteriaQuery(productCriteria, sortedPageable);

        SearchHits<ProductDocument> productResults = operations.search(productQuery, ProductDocument.class);

        List<Long> productIds = productResults.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ProductDocument::getProductId)
                .toList();

        log.info("[V2] Product ES에서 검색된 Product 개수: {}", productIds.size());
        return productIds;
    }

    @Override
    public long countDistinctProducts(InventorySearchRequest searchRequest) {
        Criteria productCriteria = buildProductCriteria(searchRequest);

        CriteriaQuery productQuery = new CriteriaQuery(productCriteria);
        SearchHits<ProductDocument> productResults = operations.search(productQuery, ProductDocument.class);

        long total = productResults.getTotalHits();
        log.info("[V2] Product ES 전체 개수: {}", total);
        return total;
    }

    /**
     * 3단계: Product ID 목록으로 Inventory 조회 (ES)
     *  - Product 조건으로 먼저 ProductId를 좁히고
     *  - Inventory 쪽 조건(수량, Lot, Location, 날짜 등)을 여기서 적용
     */
    @Override
    public SearchHits<InventoryDocument> searchByProductIds(List<Long> productIds, InventorySearchRequest searchRequest) {
        Criteria criteria = buildInventoryCriteria(searchRequest)
                .and(new Criteria("productId").in(productIds));

        // created_at 기준 내림차순 정렬
        Pageable pageable = PageRequest.of(
                0,
                10_000,
                Sort.by(Sort.Order.desc("createdAt"))
        );

        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        SearchHits<InventoryDocument> results = operations.search(query, InventoryDocument.class);
        log.info("[V2] Inventory ES에서 조회된 개수: {}", results.getSearchHits().size());

        return results;
    }

    @Override
    public SearchHits<InventoryDocument> search(InventorySearchRequest searchRequest, Pageable pageable) {
        return null;
    }

    // =========================================================
    //  조건 빌더
    // =========================================================

    /**
     * Product ES 검색 조건
     *  - productName : 부분검색 (nori)
     *  - productCode : 부분검색 (contains)
     *  - productStatus : 정확 매칭
     *  - vendorName : 부분검색
     *  - minPrice / maxPrice : unitPrice 범위
     */
    private Criteria buildProductCriteria(InventorySearchRequest request) {
        Criteria criteria = new Criteria(); // match_all

        // 상품명 (부분 검색)
        if (hasText(request.productName())) {
            String productName = request.productName();
            // 공백으로 분리 후 각각 OR 조건
            String[] keywords = productName.split("\\s+");
            Criteria nameCriteria = new Criteria();
            for (String keyword : keywords) {
                nameCriteria = nameCriteria.or(new Criteria("name").contains(keyword));
            }
            criteria = criteria.and(nameCriteria);
        }

        if (hasText(request.productCode())) {
            // Keyword 필드도 와일드카드로 부분검색 가능하게 변경
            criteria = criteria.and(new Criteria("productCode").expression("*" + request.productCode() + "*"));
        }

        // 상품 상태 (정확 매칭)
        if (hasText(request.productStatus())) {
            criteria = criteria.and(new Criteria("status").is(request.productStatus()));
        }

        // 공급사 명 (부분 검색)
        if (hasText(request.vendorName())) {
            criteria = criteria.and(new Criteria("vendorName").contains(request.vendorName()));
        }

        // 가격 범위 (unitPrice)
        if (request.minPrice() != null && request.maxPrice() != null) {
            criteria = criteria.and(new Criteria("unitPrice").between(request.minPrice(), request.maxPrice()));
        } else if (request.minPrice() != null) {
            criteria = criteria.and(new Criteria("unitPrice").greaterThanEqual(request.minPrice()));
        } else if (request.maxPrice() != null) {
            criteria = criteria.and(new Criteria("unitPrice").lessThanEqual(request.maxPrice()));
        }

        return criteria;
    }

    /**
     * Inventory ES 검색 조건
     *  - minQuantity / maxQuantity : quantity 범위
     *  - lotNumber : 부분검색
     *  - locationCode : 정확 매칭
     *  - inboundCode : 부분검색(또는 정확 매칭 원하면 is 로 변경)
     *  - inboundDateFrom / To : LocalDate 기준 날짜 범위
     *  - createdFrom / To : LocalDate 기준 날짜 범위
     */
    private Criteria buildInventoryCriteria(InventorySearchRequest request) {
        Criteria criteria = new Criteria(); // match_all

        // 수량 범위
        if (request.minQuantity() != null && request.maxQuantity() != null) {
            criteria = criteria.and(new Criteria("quantity").between(request.minQuantity(), request.maxQuantity()));
        } else if (request.minQuantity() != null) {
            criteria = criteria.and(new Criteria("quantity").greaterThanEqual(request.minQuantity()));
        } else if (request.maxQuantity() != null) {
            criteria = criteria.and(new Criteria("quantity").lessThanEqual(request.maxQuantity()));
        }

        // Lot 번호 (부분 검색)
        if (hasText(request.lotNumber())) {
            criteria = criteria.and(new Criteria("lotNumber").contains(request.lotNumber()));
        }

        // 로케이션 코드 (정확 매칭)
        if (hasText(request.locationCode())) {
            criteria = criteria.and(new Criteria("locationCode").is(request.locationCode()));
        }

        // 입고 코드 (부분 검색 허용)
        if (hasText(request.inboundCode())) {
            criteria = criteria.and(new Criteria("inboundCode").contains(request.inboundCode()));
        }

        // 입고일 범위 (LocalDate → LocalDateTime)
        if (request.inboundDateFrom() != null || request.inboundDateTo() != null) {
            LocalDateTime from = null;
            LocalDateTime to = null;

            if (request.inboundDateFrom() != null) {
                from = atStartOfDay(request.inboundDateFrom());
            }
            if (request.inboundDateTo() != null) {
                to = atEndOfDay(request.inboundDateTo());
            }

            if (from != null && to != null) {
                criteria = criteria.and(new Criteria("inboundDate").between(from, to));
            } else if (from != null) {
                criteria = criteria.and(new Criteria("inboundDate").greaterThanEqual(from));
            } else {
                criteria = criteria.and(new Criteria("inboundDate").lessThanEqual(to));
            }
        }

        // 생성일 범위 (created_at)
        if (request.createdFrom() != null || request.createdTo() != null) {
            LocalDateTime from = null;
            LocalDateTime to = null;

            if (request.createdFrom() != null) {
                from = atStartOfDay(request.createdFrom());
            }
            if (request.createdTo() != null) {
                to = atEndOfDay(request.createdTo());
            }

            if (from != null && to != null) {
                criteria = criteria.and(new Criteria("createdAt").between(from, to));
            } else if (from != null) {
                criteria = criteria.and(new Criteria("createdAt").greaterThanEqual(from));
            } else {
                criteria = criteria.and(new Criteria("createdAt").lessThanEqual(to));
            }
        }

        return criteria;
    }

    // =========================================================
    //  유틸
    // =========================================================

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }

    private LocalDateTime atStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime atEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.of(23, 59, 59, 999_999_000));
    }
}

