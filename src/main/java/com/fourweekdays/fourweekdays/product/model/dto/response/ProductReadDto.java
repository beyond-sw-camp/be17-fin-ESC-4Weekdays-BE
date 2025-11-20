package com.fourweekdays.fourweekdays.product.model.dto.response;

import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.vendor.model.dto.response.VendorReadDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductReadDto {

    private Long id;
    private String productCode;
    private String name;
    private String unit;
    private Long unitPrice;
    private String description;
    private ProductStatus status;
    private VendorReadDto vendor;
    private String vendorName; // for ES
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductReadDto from(Product product) {
        return ProductReadDto.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .name(product.getName())
                .unit(product.getUnit())
                .unitPrice(product.getUnitPrice())
                .description(product.getDescription())
                .status(product.getStatus())
                .vendor(VendorReadDto.from(product.getVendor()))
                .vendorName(product.getVendor() != null ? product.getVendor().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}