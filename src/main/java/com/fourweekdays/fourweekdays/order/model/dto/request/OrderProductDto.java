package com.fourweekdays.fourweekdays.order.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class OrderProductDto {

    @NotNull(message = "상품 ID를 입력하세요.")
    private Long productId;

    @NotNull(message = "발주 수량을 입력하세요.")
    @Min(value = 1, message = "발주 수량은 1개 이상이어야 합니다")
    private Integer orderedQuantity;

    private String description;
}
