package com.fourweekdays.fourweekdays.warehouse.repository;

import com.fourweekdays.fourweekdays.warehouse.model.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, WarehouseRepositoryCustom {
}
