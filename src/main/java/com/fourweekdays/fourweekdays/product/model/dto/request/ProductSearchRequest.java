package com.fourweekdays.fourweekdays.product.model.dto.request;

import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;

import java.time.LocalDate;

public record ProductSearchRequest(
        String productCode,
        String productName,
        ProductStatus status,
        String vendorName,

        Long minPrice,
        Long maxPrice,

        LocalDate registeredFrom,
        LocalDate registeredTo
) {
}