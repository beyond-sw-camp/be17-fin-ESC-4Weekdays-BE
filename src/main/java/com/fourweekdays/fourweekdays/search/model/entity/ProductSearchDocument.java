//package com.fourweekdays.fourweekdays.search.model.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.*;
//
//import java.util.List;
//
//// TODO 도메인 단일 책임원칙에 맞게 분산
//// 검색이라는 기능에대해 단일 책임을 지키고 있다고 할수 있을지도?
//
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Document(indexName = "product-search-test", createIndex = true)
//public class ProductSearchDocument {
//
//    @Id
//    private String id;
//
//    @Field(type = FieldType.Long, name = "vendorId")
//    private Long vendorId;
//
//    @Field(type = FieldType.Keyword, name = "vendorCode")
//    private String vendorCode;
//
//    @Field(type = FieldType.Text, name = "vendorName", analyzer = "korean_analyzer")
//    private String vendorName;
//
//    @Field(type = FieldType.Nested, name = "products")
//    private List<ProductInfo> products;
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class ProductInfo {
//
//        @Field(type = FieldType.Long, name = "productId")
//        private Long productId;
//
//        @Field(type = FieldType.Keyword, name = "productCode")
//        private String productCode;
//
//        @Field(type = FieldType.Text, name = "productName", analyzer = "korean_analyzer")
//        private String productName;
//
//        @Field(type = FieldType.Keyword, name = "productStatus")
//        private String productStatus;
//
//        @Field(type = FieldType.Integer, name = "unitPrice")
//        private Integer unitPrice;
//
//        @Field(type = FieldType.Keyword, name = "unit")
//        private String unit;
//
//        @Field(type = FieldType.Nested, name = "inbound")
//        private List<InboundInfo> inbound;
//
//        @Field(type = FieldType.Nested, name = "outbound")
//        private List<OutboundInfo> outbound;
//
//        @Field(type = FieldType.Nested, name = "inventory")
//        private List<InventoryInfo> inventory;
//
//        @Field(type = FieldType.Nested, name = "order")
//        private List<OrderInfo> order;
//
//        @Field(type = FieldType.Nested, name = "purchaseOrder")
//        private List<PurchaseOrderInfo> purchaseOrder;
//    }
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class InboundInfo {
//
//        @Field(type = FieldType.Keyword, name = "inboundCode")
//        private String inboundCode;
//
//        @Field(type = FieldType.Keyword, name = "status")
//        private String status;
//
//        @Field(type = FieldType.Date, name = "scheduledDate", format = DateFormat.date_optional_time)
//        private String scheduledDate;
//
//        @Field(type = FieldType.Text, name = "managerName", analyzer = "korean_analyzer")
//        private String managerName;
//
//        @Field(type = FieldType.Integer, name = "quantity")
//        private Integer quantity;
//
//        @Field(type = FieldType.Keyword, name = "lotNumber")
//        private String lotNumber;
//
//        @Field(type = FieldType.Keyword, name = "locationCode")
//        private String locationCode;
//    }
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class OutboundInfo {
//
//        @Field(type = FieldType.Keyword, name = "outboundCode")
//        private String outboundCode;
//
//        @Field(type = FieldType.Keyword, name = "status")
//        private String status;
//
//        @Field(type = FieldType.Keyword, name = "outboundType")
//        private String outboundType;
//
//        @Field(type = FieldType.Date, name = "scheduledDate", format = DateFormat.date_optional_time)
//        private String scheduledDate;
//
//        @Field(type = FieldType.Text, name = "managerName", analyzer = "korean_analyzer")
//        private String managerName;
//
//        @Field(type = FieldType.Integer, name = "quantity")
//        private Integer quantity;
//
//        @Field(type = FieldType.Keyword, name = "locationCode")
//        private String locationCode;
//    }
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class InventoryInfo {
//
//        @Field(type = FieldType.Long, name = "inventoryId")
//        private Long inventoryId;
//
//        @Field(type = FieldType.Keyword, name = "lotNumber")
//        private String lotNumber;
//
//        @Field(type = FieldType.Integer, name = "quantity")
//        private Integer quantity;
//
//        @Field(type = FieldType.Keyword, name = "locationCode")
//        private String locationCode;
//
//        @Field(type = FieldType.Date, name = "updatedAt", format = DateFormat.date_optional_time)
//        private String updatedAt;
//    }
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class OrderInfo {
//
//        @Field(type = FieldType.Keyword, name = "orderCode")
//        private String orderCode;
//
//        @Field(type = FieldType.Keyword, name = "status")
//        private String status;
//
//        @Field(type = FieldType.Date, name = "orderDate", format = DateFormat.date_optional_time)
//        private String orderDate;
//
//        @Field(type = FieldType.Date, name = "dueDate", format = DateFormat.date_optional_time)
//        private String dueDate;
//
//        @Field(type = FieldType.Integer, name = "quantity")
//        private Integer quantity;
//
//        @Field(type = FieldType.Text, name = "franchiseName", analyzer = "korean_analyzer")
//        private String franchiseName;
//    }
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class PurchaseOrderInfo {
//
//        @Field(type = FieldType.Keyword, name = "purchaseOrderCode")
//        private String purchaseOrderCode;
//
//        @Field(type = FieldType.Keyword, name = "status")
//        private String status;
//
//        @Field(type = FieldType.Date, name = "orderDate", format = DateFormat.date_optional_time)
//        private String orderDate;
//
//        @Field(type = FieldType.Date, name = "expectedDate", format = DateFormat.date_optional_time)
//        private String expectedDate;
//
//        @Field(type = FieldType.Integer, name = "quantity")
//        private Integer quantity;
//    }
//}