package com.fourweekdays.fourweekdays.purchaseorder.repository;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderRepositoryCustom {

    Page<PurchaseOrder> findAllWithPaging(Pageable pageable);
}
