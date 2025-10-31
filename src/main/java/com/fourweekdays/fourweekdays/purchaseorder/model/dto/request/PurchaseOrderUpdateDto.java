package com.fourweekdays.fourweekdays.purchaseorder.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderUpdateDto(
        @Future(message = "입고 예정일은 미래여야 합니다")
        LocalDateTime expectedDate,

        @Size(max = 1000, message = "비고는 1000자 이내로 입력해주세요")
        String description,

        @Valid
        List<PurchaseOrderProductRequestDto> items
) {

}
