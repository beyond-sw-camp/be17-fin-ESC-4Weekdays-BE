package com.fourweekdays.fourweekdays.inventory.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;


@Getter @NoArgsConstructor
@Builder @AllArgsConstructor
@Document(indexName = "product-inventory")
public class InventoryDocument {

    @Id
    private String id;   // ES _id 와 동일하게 String

    @Field(name = "inventoryId", type = FieldType.Long)
    private Long inventoryId;

    @Field(name = "productId", type = FieldType.Long)
    private Long productId;

    @Field(name = "productName", type = FieldType.Text, analyzer = "nori")
    private String productName;

    @Field(name = "productCode", type = FieldType.Text, analyzer = "nori")
    private String productCode;

    @Field(name = "productStatus", type = FieldType.Keyword)
    private String productStatus;

    @Field(name = "vendorName", type = FieldType.Keyword)
    private String vendorName;

    @Field(name = "unitPrice", type = FieldType.Long)
    private Long unitPrice;

    @Field(name = "unit", type = FieldType.Keyword)
    private String unit;

    @Field(name = "description", type = FieldType.Text)
    private String description;

    @Field(name = "quantity", type = FieldType.Long)
    private Long quantity;

    @Field(name = "lotNumber", type = FieldType.Keyword)
    private String lotNumber;

    @Field(name = "locationCode", type = FieldType.Keyword)
    private String locationCode;

    @Field(name = "inboundCode", type = FieldType.Keyword)
    private String inboundCode;

//    @Field(name = "inboundDate", type = FieldType.Keyword)
//    private String inboundDate;
//
//    @Field(name = "created_at", type = FieldType.Keyword)
//    private String createdAt;
    @Field(name = "inboundDate", type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime inboundDate;
    @Field(name = "created_at", type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    private LocalDateTime createdAt;

    public static InventoryDocument from(Inventory inventory) {
        return InventoryDocument.builder()
                .inventoryId(inventory.getId())
                .productId(inventory.getProduct() != null ? inventory.getProduct().getId() : null)
                .productName(inventory.getProduct() != null ? inventory.getProduct().getName() : null)
                .productCode(inventory.getProduct() != null ? inventory.getProduct().getProductCode() : null)
                .productStatus(inventory.getProduct() != null ? inventory.getProduct().getStatus().name() : null)
                .vendorName(inventory.getProduct() != null && inventory.getProduct().getVendor() != null
                        ? inventory.getProduct().getVendor().getName() : null)
                .unitPrice(inventory.getProduct() != null ? inventory.getProduct().getUnitPrice() : null)
                .unit(inventory.getProduct() != null ? inventory.getProduct().getUnit() : null)
                .description(inventory.getProduct() != null ? inventory.getProduct().getDescription() : null)
                .quantity(Long.valueOf(inventory.getQuantity()))
                .lotNumber(inventory.getLotNumber())
                .locationCode(inventory.getLocation().getLocationCode())
                .inboundDate(inventory.getInbound().getCreatedAt())
                .inboundCode(inventory.getInbound().getInboundCode())
                .createdAt(inventory.getCreatedAt())
                .build();
    }
}