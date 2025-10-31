package com.fourweekdays.fourweekdays.purchaseorder.repository;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.QPurchaseOrder;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.purchaseorder.model.entity.QPurchaseOrder.purchaseOrder;
import static com.fourweekdays.fourweekdays.vendor.model.entity.QVendor.vendor;

@RequiredArgsConstructor
public class PurchaseOrderRepositoryCustomImpl implements PurchaseOrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PurchaseOrder> findAllWithPaging(Pageable pageable) {
        List<PurchaseOrder> purchaseOrderList = queryFactory
                .selectFrom(purchaseOrder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchaseOrder.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(purchaseOrder.count())
                .from(purchaseOrder)
                .fetchOne();

        return new PageImpl<>(purchaseOrderList, pageable, total);
    }
}
