package com.fourweekdays.fourweekdays.franchise.repository;

import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.franchise.model.entity.QFranchiseStore.franchiseStore;

@RequiredArgsConstructor
public class FranchiseRepositoryCustomImpl implements FranchiseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FranchiseStore> findAllWithPaging(Pageable pageable) {
        List<FranchiseStore> content = queryFactory
                .selectFrom(franchiseStore)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(franchiseStore.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(franchiseStore.count())
                .from(franchiseStore)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
