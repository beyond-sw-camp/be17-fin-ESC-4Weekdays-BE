package com.fourweekdays.fourweekdays.warehouse.repository;

import com.fourweekdays.fourweekdays.warehouse.model.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseRepositoryCustom {
    Page<Warehouse> findAllWithPaging(Pageable pageable);
}
