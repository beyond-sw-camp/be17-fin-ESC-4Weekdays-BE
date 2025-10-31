package com.fourweekdays.fourweekdays.inventory.repository;

import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchRequest;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.ProductInventoryResponse;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InventoryRepositoryCustom {

    Page<Inventory> searchInventory(Pageable pageable, InventorySearchRequest request);

    Page<ProductInventoryResponse> searchInventoryByProduct(Pageable pageable, InventorySearchRequest request);

    Optional<ProductInventoryResponse> findDetailByProductCode(String productCode);
}
