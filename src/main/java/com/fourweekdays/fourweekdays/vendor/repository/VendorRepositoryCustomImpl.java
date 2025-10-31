package com.fourweekdays.fourweekdays.vendor.repository;

import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.vendor.model.entity.QVendor.vendor;

@RequiredArgsConstructor
public class VendorRepositoryCustomImpl implements VendorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Vendor> findAllWithPaging(Pageable pageable) {
        List<Vendor> content = queryFactory
                .selectFrom(vendor)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vendor.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(vendor.count())
                .from(vendor)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
