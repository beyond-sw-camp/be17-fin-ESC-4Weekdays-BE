package com.fourweekdays.fourweekdays.search.test;

import com.fourweekdays.fourweekdays.search.ProductSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSearchRepository extends
        ElasticsearchRepository<ProductSearchDocument, String>, OrderSearchRepositoryCustom {
}