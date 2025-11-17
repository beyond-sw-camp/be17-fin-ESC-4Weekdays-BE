//package com.fourweekdays.fourweekdays.search.repository;
//
//import co.elastic.clients.elasticsearch._types.FieldValue;
//import co.elastic.clients.elasticsearch._types.query_dsl.*;
//import co.elastic.clients.elasticsearch.core.search.InnerHits;
//import com.fourweekdays.fourweekdays.search.exception.SearchException;
//import com.fourweekdays.fourweekdays.search.model.entity.ProductSearchDocument;
//import com.fourweekdays.fourweekdays.search.model.dto.request.SearchRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.client.elc.NativeQuery;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static com.fourweekdays.fourweekdays.search.exception.SearchExceptionType.DOMAIN_NOT_FOUND;
//
//@Slf4j
//@Repository
//@RequiredArgsConstructor
//public class SearchRepositoryImpl implements SearchRepositoryCustom {
//
//    private final ElasticsearchOperations elasticsearchOperations;
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00");
//
//    @Override
//    public SearchHits<ProductSearchDocument> unifiedSearch(SearchRequest condition) {
//        // 1. 도메인 검증
//        validateDomain(condition.getDomain());
//
//        // 2. Root Query 생성
//        Query rootQuery = buildRootQuery(condition);
//
//        // 3. Source Filtering
//        FetchSourceFilter sourceFilter = createSourceFilter(condition.getDomain());
//
//        // 4. Pageable
//        Pageable pageable = createPageable(condition);
//
//        // 5. NativeQuery 생성
//        NativeQuery query = NativeQuery.builder()
//                .withQuery(rootQuery)
//                .withSourceFilter(sourceFilter)
//                .withPageable(pageable)
//                .build();
//
//        // 6. 실행
//        SearchHits<ProductSearchDocument> searchHits =
//                elasticsearchOperations.search(query, ProductSearchDocument.class);
//
//        log.info("통합 검색 완료 - 도메인: {}, 결과: {} 건",
//                condition.getDomain(), searchHits.getTotalHits());
//
//        return searchHits;
//    }
//
//    private void validateDomain(String domain) {
//        // TODO 도메인 없으면 통합 검색
//
//        String[] validDomains = {"inbound", "outbound", "order", "inventory", "purchaseOrder"};
//        for (String valid : validDomains) {
//            if (valid.equalsIgnoreCase(domain)) {
//                return;
//            }
//        }
//        throw new SearchException(DOMAIN_NOT_FOUND);
//    }
//
//    /**
//     * Root Query 생성 (Vendor + Product + Nested)
//     */
//    private Query buildRootQuery(SearchRequest condition) {
//        List<Query> mustQueries = new ArrayList<>();
//
//        // ========================================
//        // Vendor 조건
//        // ========================================
//        if (condition.getVendorId() != null) {
//            TermQuery vendorIdQuery = new TermQuery.Builder()
//                    .field("vendorId")
//                    .value(condition.getVendorId())
//                    .build();
//            mustQueries.add(vendorIdQuery._toQuery());
//        }
//
//        if (StringUtils.hasText(condition.getVendorCode())) {
//            TermQuery vendorCodeQuery = new TermQuery.Builder()
//                    .field("vendorCode")
//                    .value(condition.getVendorCode())
//                    .build();
//            mustQueries.add(vendorCodeQuery._toQuery());
//        }
//
//        if (StringUtils.hasText(condition.getVendorCodePrefix())) {
//            PrefixQuery vendorPrefixQuery = new PrefixQuery.Builder()
//                    .field("vendorCode")
//                    .value(condition.getVendorCodePrefix().toUpperCase())
//                    .build();
//            mustQueries.add(vendorPrefixQuery._toQuery());
//        }
//
//        if (StringUtils.hasText(condition.getVendorName())) {
//            MatchQuery vendorNameQuery = new MatchQuery.Builder()
//                    .field("vendorName")
//                    .query(condition.getVendorName())
//                    .build();
//            mustQueries.add(vendorNameQuery._toQuery());
//        }
//
//        // ========================================
//        // Product 조건
//        // ========================================
//        if (condition.getProductId() != null) {
//            TermQuery productIdQuery = new TermQuery.Builder()
//                    .field("products.productId")
//                    .value(condition.getProductId())
//                    .build();
//            mustQueries.add(productIdQuery._toQuery());
//        }
//
//        if (StringUtils.hasText(condition.getProductCode())) {
//            TermQuery productCodeQuery = new TermQuery.Builder()
//                    .field("products.productCode")
//                    .value(condition.getProductCode())
//                    .build();
//            mustQueries.add(productCodeQuery._toQuery());
//        }
//
//        if (StringUtils.hasText(condition.getProductName())) {
//            MatchQuery productNameQuery = new MatchQuery.Builder()
//                    .field("products.productName")
//                    .query(condition.getProductName())
//                    .build();
//            mustQueries.add(productNameQuery._toQuery());
//        }
//
//        if (StringUtils.hasText(condition.getProductStatus())) {
//            TermQuery productStatusQuery = new TermQuery.Builder()
//                    .field("products.productStatus")
//                    .value(condition.getProductStatus())
//                    .build();
//            mustQueries.add(productStatusQuery._toQuery());
//        }
//
//        // ========================================
//        // Nested Query 추가
//        // ========================================
//        Query nestedQuery = buildNestedQuery(condition);
//        mustQueries.add(nestedQuery);
//
//        // ========================================
//        // BoolQuery로 조합
//        // ========================================
//        BoolQuery boolQuery = new BoolQuery.Builder()
//                .must(mustQueries)
//                .build();
//
//        return boolQuery._toQuery();
//    }
//
//    /**
//     * Nested Query 생성
//     */
//    private Query buildNestedQuery(SearchRequest condition) {
//        String domain = condition.getDomain().toLowerCase();
//        String nestedPath = "products." + domain;
//
//        // Domain 조건 생성
//        Query domainQuery = buildDomainConditions(condition, domain);
//
//        // Inner Hits 생성
//        InnerHits innerHits = buildInnerHits(condition, domain);
//
//        // NestedQuery 생성
//        NestedQuery nestedQuery = new NestedQuery.Builder()
//                .path(nestedPath)
//                .query(domainQuery)
//                .innerHits(innerHits)
//                .build();
//
//        return nestedQuery._toQuery();
//    }
//
//    /**
//     * Domain 조건 생성
//     */
//    private Query buildDomainConditions(SearchRequest condition, String domain) {
//        List<Query> mustQueries = new ArrayList<>();
//        String pathPrefix = "products." + domain + ".";
//
//        // ========================================
//        // Code 필드 (keyword)
//        // ========================================
//        if (StringUtils.hasText(condition.getCode())) {
//            String codeField = pathPrefix + getCodeFieldName(domain);
//            TermQuery codeQuery = new TermQuery.Builder()
//                    .field(codeField)
//                    .value(condition.getCode())
//                    .build();
//            mustQueries.add(codeQuery._toQuery());
//        }
//
//        // ========================================
//        // Status 필드 (keyword, 복수)
//        // ========================================
//        if (condition.getStatuses() != null && !condition.getStatuses().isEmpty()) {
//            List<FieldValue> statusValues = condition.getStatuses().stream()
//                    .map(FieldValue::of)
//                    .collect(Collectors.toList());
//
//            TermsQuery statusQuery = new TermsQuery.Builder()
//                    .field(pathPrefix + "status")
//                    .terms(t -> t.value(statusValues))
//                    .build();
//            mustQueries.add(statusQuery._toQuery());
//        }
//
//        // ========================================
//        // Date 필드 (date range) - 핵심!
//        // ========================================
//        if (condition.getDateFrom() != null || condition.getDateTo() != null) {
//            String dateField = pathPrefix + getDateFieldName(domain);
//
//            RangeQuery dateRangeQuery = new RangeQuery.Builder()
//                    .date(d -> {
//                        d.field(dateField);
//                        if (condition.getDateFrom() != null) {
//                            d.gte(condition.getDateFrom().format(DATE_FORMATTER));
//                        }
//                        if (condition.getDateTo() != null) {
//                            d.lte(condition.getDateTo().format(DATE_FORMATTER));
//                        }
//                        return d;
//                    })
//                    .build();
//
//            mustQueries.add(dateRangeQuery._toQuery());
//        }
//
//        // ========================================
//        // Name 필드 (text, 한글 검색)
//        // ========================================
//        if (StringUtils.hasText(condition.getName())) {
//            String nameField = getNameFieldName(domain);
//            if (nameField != null) {
//                MatchQuery nameQuery = new MatchQuery.Builder()
//                        .field(pathPrefix + nameField)
//                        .query(condition.getName())
//                        .build();
//                mustQueries.add(nameQuery._toQuery());
//            }
//        }
//
//        // ========================================
//        // Quantity 필드 (number range) - 핵심!
//        // ========================================
//        if (condition.getQuantityMin() != null || condition.getQuantityMax() != null) {
//            RangeQuery quantityRangeQuery = new RangeQuery.Builder()
//                    .number(n -> {
//                        n.field(pathPrefix + "quantity");
//                        if (condition.getQuantityMin() != null) {
//                            n.gte(condition.getQuantityMin().doubleValue());
//                        }
//                        if (condition.getQuantityMax() != null) {
//                            n.lte(condition.getQuantityMax().doubleValue());
//                        }
//                        return n;
//                    })
//                    .build();
//
//            mustQueries.add(quantityRangeQuery._toQuery());
//        }
//
//        // ========================================
//        // Domain별 특수 필드
//        // ========================================
//        addDomainSpecificConditions(mustQueries, condition, domain, pathPrefix);
//
//        // ========================================
//        // BoolQuery로 조합
//        // ========================================
//        if (mustQueries.isEmpty()) {
//            MatchAllQuery matchAll = new MatchAllQuery.Builder().build();
//            return matchAll._toQuery();
//        }
//
//        BoolQuery boolQuery = new BoolQuery.Builder()
//                .must(mustQueries)
//                .build();
//
//        return boolQuery._toQuery();
//    }
//
//    /**
//     * Domain별 특수 조건 추가
//     */
//    private void addDomainSpecificConditions(
//            List<Query> mustQueries,
//            SearchRequest condition,
//            String domain,
//            String pathPrefix
//    ) {
//        switch (domain) {
//            case "outbound":
//                // outboundType (keyword)
//                if (StringUtils.hasText(condition.getOutboundType())) {
//                    TermQuery outboundTypeQuery = new TermQuery.Builder()
//                            .field(pathPrefix + "outboundType")
//                            .value(condition.getOutboundType())
//                            .build();
//                    mustQueries.add(outboundTypeQuery._toQuery());
//                }
//                // locationCode (keyword)
//                if (StringUtils.hasText(condition.getLocationCode())) {
//                    TermQuery locationQuery = new TermQuery.Builder()
//                            .field(pathPrefix + "locationCode")
//                            .value(condition.getLocationCode())
//                            .build();
//                    mustQueries.add(locationQuery._toQuery());
//                }
//                break;
//
//            case "inbound":
//                // locationCode (keyword)
//                if (StringUtils.hasText(condition.getLocationCode())) {
//                    TermQuery locationQuery = new TermQuery.Builder()
//                            .field(pathPrefix + "locationCode")
//                            .value(condition.getLocationCode())
//                            .build();
//                    mustQueries.add(locationQuery._toQuery());
//                }
//                // lotNumber (keyword)
//                if (StringUtils.hasText(condition.getLotNumber())) {
//                    TermQuery lotQuery = new TermQuery.Builder()
//                            .field(pathPrefix + "lotNumber")
//                            .value(condition.getLotNumber())
//                            .build();
//                    mustQueries.add(lotQuery._toQuery());
//                }
//                break;
//
//            case "inventory":
//                // locationCode (keyword)
//                if (StringUtils.hasText(condition.getLocationCode())) {
//                    TermQuery locationQuery = new TermQuery.Builder()
//                            .field(pathPrefix + "locationCode")
//                            .value(condition.getLocationCode())
//                            .build();
//                    mustQueries.add(locationQuery._toQuery());
//                }
//                break;
//
//            case "order":
//                // dueDate 범위 (date range)
//                if (condition.getDueDateFrom() != null || condition.getDueDateTo() != null) {
//                    RangeQuery dueDateRangeQuery = new RangeQuery.Builder()
//                            .date(d -> {
//                                d.field(pathPrefix + "dueDate");
//                                if (condition.getDueDateFrom() != null) {
//                                    d.gte(condition.getDueDateFrom().format(DATE_FORMATTER));
//                                }
//                                if (condition.getDueDateTo() != null) {
//                                    d.lte(condition.getDueDateTo().format(DATE_FORMATTER));
//                                }
//                                return d;
//                            })
//                            .build();
//
//                    mustQueries.add(dueDateRangeQuery._toQuery());
//                }
//                break;
//        }
//    }
//
//    /**
//     * Inner Hits 생성
//     */
//    private InnerHits buildInnerHits(SearchRequest condition, String domain) {
//        int innerHitsSize = condition.getInnerHitsSize() != null ?
//                condition.getInnerHitsSize() : 100;
//
//        String[] sourceFields = getInnerHitsSourceFields(domain);
//
//        return new InnerHits.Builder()
//                .name("matched_" + domain)
//                .size(innerHitsSize)
//                .source(s -> s
//                        .filter(f -> f
//                                .includes(List.of(sourceFields))
//                        )
//                )
//                .build();
//    }
//
//    /**
//     * Domain별 Code 필드명
//     */
//    private String getCodeFieldName(String domain) {
//        switch (domain) {
//            case "inbound":
//                return "inboundCode";
//            case "outbound":
//                return "outboundCode";
//            case "order":
//                return "orderCode";
//            case "purchaseorder":
//                return "purchaseOrderCode";
//            case "inventory":
//                return "lotNumber";
//            default:
//                return "code";
//        }
//    }
//
//    /**
//     * Domain별 Date 필드명
//     */
//    private String getDateFieldName(String domain) {
//        switch (domain) {
//            case "inbound":
//            case "outbound":
//                return "scheduledDate";
//            case "order":
//            case "purchaseorder":
//                return "orderDate";
//            case "inventory":
//                return "updatedAt";
//            default:
//                return "date";
//        }
//    }
//
//    /**
//     * Domain별 Name 필드명
//     */
//    private String getNameFieldName(String domain) {
//        switch (domain) {
//            case "inbound":
//            case "outbound":
//                return "managerName";
//            case "order":
//                return "franchiseName";
//            default:
//                return null;
//        }
//    }
//
//    /**
//     * Inner Hits Source Fields
//     */
//    private String[] getInnerHitsSourceFields(String domain) {
//        switch (domain) {
//            case "inbound":
//                return new String[]{
//                        "inboundCode", "status", "scheduledDate",
//                        "managerName", "quantity", "lotNumber", "locationCode"
//                };
//            case "outbound":
//                return new String[]{
//                        "outboundCode", "status", "outboundType", "scheduledDate",
//                        "managerName", "quantity", "locationCode"
//                };
//            case "order":
//                return new String[]{
//                        "orderCode", "status", "orderDate", "dueDate",
//                        "quantity", "franchiseName"
//                };
//            case "inventory":
//                return new String[]{
//                        "inventoryId", "lotNumber", "quantity",
//                        "locationCode", "updatedAt"
//                };
//            case "purchaseorder":
//                return new String[]{
//                        "purchaseOrderCode", "status", "orderDate",
//                        "expectedDate", "quantity"
//                };
//            default:
//                return new String[]{"*"};
//        }
//    }
//
//    /**
//     * Source Filtering
//     */
//    private FetchSourceFilter createSourceFilter(String domain) {
//        return new FetchSourceFilter(
//                true,
//                new String[]{
//                        "vendorId", "vendorCode", "vendorName",
//                        "products.productId", "products.productCode",
//                        "products.productName", "products.productStatus"
//                },
//                null
//        );
//    }
//
//    /**
//     * Pageable 생성
//     */
//    private Pageable createPageable(SearchRequest condition) {
//        int page = condition.getPage() != null ? condition.getPage() : 0;
//        int size = condition.getSize() != null ? condition.getSize() : 20;
//        return PageRequest.of(page, size);
//    }
//}