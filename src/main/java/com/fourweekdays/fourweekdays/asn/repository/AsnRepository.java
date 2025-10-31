package com.fourweekdays.fourweekdays.asn.repository;

import com.fourweekdays.fourweekdays.asn.model.entity.Asn;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsnRepository extends JpaRepository<Asn, Long>, AsnRepositoryCustom {

    boolean existsByPurchaseOrder(PurchaseOrder purchaseOrder);
}

