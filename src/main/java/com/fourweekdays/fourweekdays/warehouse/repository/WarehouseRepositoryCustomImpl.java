package com.fourweekdays.fourweekdays.warehouse.repository;

import com.fourweekdays.fourweekdays.warehouse.model.entity.Warehouse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.warehouse.model.entity.QWarehouse.warehouse;

@RequiredArgsConstructor
public class WarehouseRepositoryCustomImpl implements WarehouseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Warehouse> findAllWithPaging(Pageable pageable) {
        List<Warehouse> content = queryFactory
                .selectFrom(warehouse)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(warehouse.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(warehouse.count())
                .from(warehouse)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
