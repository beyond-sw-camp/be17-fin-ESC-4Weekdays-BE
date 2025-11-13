package com.fourweekdays.fourweekdays.search.test;

import com.fourweekdays.fourweekdays.search.ProductSearchDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

public interface OrderSearchRepositoryCustom {
    SearchHits<ProductSearchDocument> searchOrders(OrderSearchCondition condition);
}