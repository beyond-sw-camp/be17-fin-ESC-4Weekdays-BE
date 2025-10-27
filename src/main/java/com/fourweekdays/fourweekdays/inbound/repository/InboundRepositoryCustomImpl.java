package com.fourweekdays.fourweekdays.inbound.repository;

import com.fourweekdays.fourweekdays.inbound.model.dto.response.InboundReadDto;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.QInbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.QInboundProduct;
import com.fourweekdays.fourweekdays.product.model.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.fourweekdays.fourweekdays.inbound.model.entity.QInbound.inbound;

@RequiredArgsConstructor
public class InboundRepositoryCustomImpl implements InboundRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Inbound> findAllWithPaging(Pageable pageable) {
        List<Inbound> inbounds = queryFactory
                .selectFrom(inbound)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inbound.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(inbound.count())
                .from(inbound)
                .fetchOne();

        return new PageImpl<>(inbounds, pageable, total);
    }

    @Override
    public Page<InboundReadDto> findByProductName(String productName, Pageable pageable) {
        QInbound inbound = QInbound.inbound;
        QInboundProduct inboundProduct = QInboundProduct.inboundProduct;
        QProduct product = QProduct.product;

        // 데이터 조회
        List<Inbound> results = queryFactory
                .selectFrom(inbound)
                .distinct()
                .leftJoin(inbound.products, inboundProduct).fetchJoin()
                .leftJoin(inboundProduct.product, product).fetchJoin()
                .where(eqProductName(productName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(inbound.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(inbound.countDistinct())
                .from(inbound)
                .leftJoin(inbound.products, inboundProduct)
                .leftJoin(inboundProduct.product, product)
                .where(eqProductName(productName))
                .fetchOne();

        // DTO 변환
        List<InboundReadDto> dtoList = results.stream()
                .map(InboundReadDto::from)
                .toList();

        return new PageImpl<>(dtoList, pageable, total != null ? total : 0L);
    }

    private BooleanExpression eqProductName(String productName) {
        if (!StringUtils.hasText(productName)) return null;
        QProduct product = QProduct.product;
        return product.name.containsIgnoreCase(productName);
    }
}
