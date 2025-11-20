package com.fourweekdays.fourweekdays.product.model.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
@Builder @AllArgsConstructor
@Document(indexName = "products")
public class ProductDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String name;

    @Field(name = "product_code", type = FieldType.Keyword)
    private String productCode;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String unit;

    @Field(name = "unit_price", type = FieldType.Long)
    private Long unitPrice;

    @Field(type = FieldType.Keyword)
    private ProductStatus status;

    @Field(name = "vendor_id", type = FieldType.Long)
    private Long vendorId;

    @Field(name = "vendor_name", type = FieldType.Keyword)
    private String vendorName;

    @Field(name = "created_at", type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime createdAt;

    @Field(name = "updated_at", type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime updatedAt;

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .id(product.getId())
                .name(product.getName())
                .productCode(product.getProductCode())
                .description(product.getDescription())
                .unit(product.getUnit())
                .unitPrice(product.getUnitPrice())
                .status(product.getStatus())
                .vendorId(product.getVendor() != null ? product.getVendor().getId() : null)
                .vendorName(product.getVendor() != null ? product.getVendor().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

}