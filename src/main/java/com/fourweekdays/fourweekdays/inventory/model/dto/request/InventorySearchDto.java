package com.fourweekdays.fourweekdays.inventory.model.dto.request;

import lombok.Getter;

@Getter
public class InventorySearchDto {
    //    private Product product; // 상품 상세 조건들
    private String productId; // 재고 번호
    @Schema(description = "보관 위치 (색션)", example = "A-12")
    private String location; // 보관 위치
    @Schema(description = "물건의 총 수량", example = "35")
    private int quantity; // 수량
    @Schema(description = "적치 작업자 이름", example = "홍길동")
    private String memberName; // 작업자
    @Schema(description = "재고 생성일", example = "yyyy-mm-dd")
    private Integer createAt; // 기간

//    입고/출고와 관계를 맺는다면
//    private Integer inboundAt;
//    private Integer outboundAt;
}
