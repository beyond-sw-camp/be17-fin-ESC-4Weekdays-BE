package com.fourweekdays.fourweekdays.asn.repository;

import com.fourweekdays.fourweekdays.asn.model.entity.Asn;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.asn.model.entity.QAsn.asn;


@RequiredArgsConstructor
public class AsnRepositoryCustomImpl implements AsnRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Asn> findAllWithPaging(Pageable pageable) {
        
        List<Asn> result = queryFactory.selectFrom(asn)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(asn.createdAt.desc())
                .fetch();

        Long total = queryFactory.select(asn.count())
                .from(asn)
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }
}
