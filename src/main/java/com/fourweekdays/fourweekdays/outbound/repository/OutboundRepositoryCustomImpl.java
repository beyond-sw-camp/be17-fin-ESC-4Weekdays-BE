package com.fourweekdays.fourweekdays.outbound.repository;

import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.QOutbound;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.fourweekdays.fourweekdays.inbound.model.entity.QInbound.inbound;
import static com.fourweekdays.fourweekdays.inbound.model.entity.QInboundProduct.inboundProduct;
import static com.fourweekdays.fourweekdays.outbound.model.entity.QOutboundProductItem.outboundProductItem;
import static com.fourweekdays.fourweekdays.product.model.entity.QProduct.product;
import static com.fourweekdays.fourweekdays.purchaseorder.model.entity.QPurchaseOrder.purchaseOrder;
import static com.fourweekdays.fourweekdays.vendor.model.entity.QVendor.vendor;

@Repository
@RequiredArgsConstructor
public class OutboundRepositoryCustomImpl implements OutboundRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QOutbound outbound = QOutbound.outbound;

    @Override
    public Page<Outbound> findAllWithPaging(Pageable pageable) {
        List<Outbound> outbounds = queryFactory
                .selectFrom(outbound)
                .leftJoin(outbound.items, outboundProductItem).fetchJoin()             // OutboundProductItem
                .leftJoin(outboundProductItem.product, product).fetchJoin()     // Product
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(outbound.createdAt.desc())
                .distinct()                                       // 중복 제거 필수
                .fetch();

        Long total = queryFactory
                .select(outbound.count())
                .from(outbound)
                .fetchOne();

        return new PageImpl<>(outbounds, pageable, total);
    }
}
