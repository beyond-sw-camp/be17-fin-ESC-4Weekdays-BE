package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.dto.request.ProductSearchRequest;
import com.fourweekdays.fourweekdays.product.model.entity.ProductDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;


@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductSearchRepositoryImpl implements ProductSearchRepository {

    private final ElasticsearchOperations operations;

    @Override
    public SearchHits<ProductDocument> search(ProductSearchRequest searchRequest, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (searchRequest.productName() != null && !searchRequest.productName().isEmpty()) {
            criteria = criteria.and(new Criteria("name").matches(searchRequest.productName()));
        }

        if (searchRequest.productCode() != null && !searchRequest.productCode().isEmpty()) {
            criteria = criteria.and(new Criteria("productCode").is(searchRequest.productCode()));
        }

        if (searchRequest.status() != null) {
            criteria = criteria.and(new Criteria("status").is(searchRequest.status().name()));
        }

        if (searchRequest.vendorName() != null && !searchRequest.vendorName().isEmpty()) {
            criteria = criteria.and(new Criteria("vendorName").contains(searchRequest.vendorName()));
        }

        if (searchRequest.minPrice() != null && searchRequest.maxPrice() != null) {
            criteria = criteria.and(new Criteria("unitPrice").between(searchRequest.minPrice(), searchRequest.maxPrice()));
        } else if (searchRequest.minPrice() != null) {
            criteria = criteria.and(new Criteria("unitPrice").greaterThanEqual(searchRequest.minPrice()));
        } else if (searchRequest.maxPrice() != null) {
            criteria = criteria.and(new Criteria("unitPrice").lessThanEqual(searchRequest.maxPrice()));
        }

        if (searchRequest.registeredFrom() != null && searchRequest.registeredTo() != null) {
            criteria = criteria.and(new Criteria("createdAt").between(
                    searchRequest.registeredFrom().atStartOfDay(),
                    searchRequest.registeredTo().atTime(23, 59, 59)
            ));
        } else if (searchRequest.registeredFrom() != null) {
            criteria = criteria.and(new Criteria("createdAt").greaterThanEqual(
                    searchRequest.registeredFrom().atStartOfDay()
            ));
        } else if (searchRequest.registeredTo() != null) {
            criteria = criteria.and(new Criteria("createdAt").lessThanEqual(
                    searchRequest.registeredTo().atTime(23, 59, 59)
            ));
        }

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("id"))
        );
        CriteriaQuery query = new CriteriaQuery(criteria, sortedPageable);
        SearchHits<ProductDocument> results = operations.search(query, ProductDocument.class);

        log.info("ES 조회 결과: {} / {}", results.getSearchHits().size(), results.getTotalHits());

        return results;
    }
}