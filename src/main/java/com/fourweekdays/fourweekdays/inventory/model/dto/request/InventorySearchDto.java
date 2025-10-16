package com.fourweekdays.fourweekdays.inventory.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "검색 필터 DTO")
public class InventorySearchDto {

    //    private Product product; // 상품 상세 조건들
    @Schema(description = "재고 번호", example = "STK-001")
    private String inventoryId;
    @Schema(description = "보관 위치 (색션)", example = "A-12")
    private String location;
    @Schema(description = "물건의 총 수량", example = "35")
    private int quantity;
    @Schema(description = "적치 작업자 이름", example = "홍길동")
    private String workerName;
    @Schema(description = "재고 생성일", example = "yyyy-mm-dd")
    private LocalDate createAt;

//    입고/출고와 관계를 맺는다면
//    private Integer inboundAt;
//    private Integer outboundAt;
}
