package com.fourweekdays.fourweekdays.order.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderReceiveOrderDto {

    @NotNull(message = "납기 예정일을 입력해주세요")
    private LocalDateTime dueDate;

    @Size(max = 1000, message = "비고는 1000자 이내로 입력해주세요")
    private String description;

    @NotNull(message = "상품을 선택해주세요")
    private List<OrderProductDto> items;
}
