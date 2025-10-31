package com.fourweekdays.fourweekdays.purchaseorder.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PurchaseOrderProductRequestDto {

    @Schema(description = "상품 ID", example = "PRD-1001")
    @NotNull(message = "상품 ID를 입력하세요.")
    private Long productId;

    @Schema(description = "발주수량", example = "50")
    @NotNull(message = "발주 수량을 입력하세요.")
    @Min(value = 1, message = "발주 수량은 1개 이상이어야 합니다")
    private Integer orderedQuantity;

    @Schema(description = "비고 입력", example = "비고 입력입니다.")
    @Size(max = 500, message = "비고는 500자 이하로 입력해주세요")
    private String description;
}