package com.fourweekdays.fourweekdays.order.repository;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.order.model.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findAllWithPaging(Pageable pageable) {
        List<Order> result = queryFactory.selectFrom(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order.createdAt.desc())
                .fetch();

        Long total = queryFactory.select(order.count())
                .from(order)
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }
}
