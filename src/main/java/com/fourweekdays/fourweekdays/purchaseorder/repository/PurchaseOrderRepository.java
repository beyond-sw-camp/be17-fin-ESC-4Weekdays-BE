package com.fourweekdays.fourweekdays.purchaseorder.repository;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>, PurchaseOrderRepositoryCustom {
    Optional<PurchaseOrder> findByOrderCode(String orderCode);
}
