package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.dto.request.ProductSearchRequest;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

import static com.fourweekdays.fourweekdays.product.model.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public Page<Product> searchProducts(Pageable pageable, ProductSearchRequest request) {
        List<Product> results = queryFactory
                .selectFrom(product)
                .leftJoin(product.vendor).fetchJoin()
                .where(
                        productCodeEq(request.productCode()),
                        productNameLike(request.productName()),
                        productStatusEq(request.status()),
                        vendorNameContains(request.vendorName()),
                        unitPriceBetween(request.minPrice(), request.maxPrice()),
                        createdAtBetween(request.registeredFrom(), request.registeredTo())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.id.desc())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.vendor)
                .where(
                        productCodeEq(request.productCode()),
                        productNameLike(request.productName()),
                        productStatusEq(request.status()),
                        vendorNameContains(request.vendorName()),
                        unitPriceBetween(request.minPrice(), request.maxPrice()),
                        createdAtBetween(request.registeredFrom(), request.registeredTo())
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    private BooleanExpression productCodeEq(String productCode) {
        return StringUtils.hasText(productCode) ? product.productCode.eq(productCode) : null;
    }

    private BooleanExpression productNameLike(String productName) {
        return StringUtils.hasText(productName) ? product.name.contains(productName) : null;
    }

    public static BooleanExpression productStatusEq(ProductStatus status) {
        return status != null ? product.status.eq(status) : null;
    }

    public static BooleanExpression vendorNameContains(String vendorName) {
        return StringUtils.hasText(vendorName) ? product.vendor.name.containsIgnoreCase(vendorName) : null;
    }

    private BooleanExpression unitPriceBetween(Long minPrice, Long maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return product.unitPrice.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return product.unitPrice.goe(minPrice);
        } else if (maxPrice != null) {
            return product.unitPrice.loe(maxPrice);
        }
        return null;
    }

    private BooleanExpression createdAtBetween(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return product.createdAt.between(
                    from.atStartOfDay(),
                    to.atTime(23, 59, 59)
            );
        }
        if (from != null) {
            return product.createdAt.goe(from.atStartOfDay());
        }
        if (to != null) {
            return product.createdAt.loe(to.atTime(23, 59, 59));
        }
        return null;
    }
}
