package com.fourweekdays.fourweekdays.search.controller.repository;

import com.fourweekdays.fourweekdays.search.controller.model.entity.ProductSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends
        ElasticsearchRepository<ProductSearchDocument, String>,
        SearchRepositoryCustom {
}
