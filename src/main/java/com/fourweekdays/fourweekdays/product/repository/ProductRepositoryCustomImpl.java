package com.fourweekdays.fourweekdays.product.repository;

import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.product.model.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> findAllWithPaging(Pageable pageable) {
        List<Product> products = queryFactory
                .selectFrom(product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(product.count())
                .from(product)
                .fetchOne();

        return new PageImpl<>(products, pageable, total);
    }
}
