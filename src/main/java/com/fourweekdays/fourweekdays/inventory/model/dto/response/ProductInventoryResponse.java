package com.fourweekdays.fourweekdays.inventory.model.dto.response;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.product.model.entity.Product;

import java.util.List;


public record ProductInventoryResponse(
        Long productId,
        String productCode,
        String productName,
        long unitPrice,
        String vendorName,
        int totalQuantity,
        long totalPrice,
        List<ProductInventoryDetailResponse> inventories
) {
    public static ProductInventoryResponse from(Product product, List<Inventory> inventories) {
        int totalQuantity = inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

        long totalPrice = totalQuantity * product.getUnitPrice();

        return new ProductInventoryResponse(
                product.getId(),
                product.getProductCode(),
                product.getName(),
                product.getUnitPrice(),
                product.getVendor().getName(),
                totalQuantity,
                totalPrice,
                inventories.stream()
                        .map(ProductInventoryDetailResponse::from)
                        .toList()
        );
    }
}
