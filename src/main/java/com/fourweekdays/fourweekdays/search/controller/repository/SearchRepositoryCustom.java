package com.fourweekdays.fourweekdays.search.controller.repository;

import com.fourweekdays.fourweekdays.search.controller.model.entity.ProductSearchDocument;
import com.fourweekdays.fourweekdays.search.controller.model.dto.request.SearchRequest;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface SearchRepositoryCustom {
    SearchHits<ProductSearchDocument> unifiedSearch(SearchRequest condition);
}
