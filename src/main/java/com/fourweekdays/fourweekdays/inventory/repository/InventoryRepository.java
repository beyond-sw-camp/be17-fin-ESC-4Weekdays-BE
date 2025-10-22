package com.fourweekdays.fourweekdays.inventory.repository;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.model.entity.Location;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndLocation(Product product, Location location);
}
