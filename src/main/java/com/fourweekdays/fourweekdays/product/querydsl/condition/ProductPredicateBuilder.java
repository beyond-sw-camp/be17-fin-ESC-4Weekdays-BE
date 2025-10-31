package com.fourweekdays.fourweekdays.product.querydsl.condition;

import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.model.entity.QProduct;
import com.querydsl.core.BooleanBuilder;
import org.springframework.util.StringUtils;

import java.util.List;

public class ProductPredicateBuilder {

    private static final QProduct product = QProduct.product;

    public static BooleanBuilder buildProductPredicate(
            String code,
            String name,
            String unit,
            List<Long> vendorIds,
            Long minPrice,
            Long maxPrice,
            ProductStatus status
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(code)) builder.and(product.productCode.containsIgnoreCase(code));
        if (StringUtils.hasText(name)) builder.and(product.name.containsIgnoreCase(name));
        if (StringUtils.hasText(unit)) builder.and(product.unit.eq(unit));

        if (vendorIds != null && !vendorIds.isEmpty())
            builder.and(product.vendor.id.in(vendorIds));

        if (minPrice != null) builder.and(product.unitPrice.goe(minPrice));
        if (maxPrice != null) builder.and(product.unitPrice.loe(maxPrice));

        if (status != null) builder.and(product.status.eq(status));

        return builder;
    }
}