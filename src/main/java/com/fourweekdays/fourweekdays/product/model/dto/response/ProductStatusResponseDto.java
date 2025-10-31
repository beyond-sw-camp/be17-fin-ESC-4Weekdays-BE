package com.fourweekdays.fourweekdays.product.model.dto.response;

import com.fourweekdays.fourweekdays.product.model.entity.ProductStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusResponseDto {
    private Long productId;
    private String productName;
    private String oldStatus;
    private String newStatus;
    private String changedBy;
    private LocalDateTime changedAt;

    public static ProductStatusResponseDto from(ProductStatusHistory history) {
        return ProductStatusResponseDto.builder()
                .productId(history.getProduct().getId())
                .productName(history.getProduct().getName())
                .oldStatus(history.getOldStatus() != null ? history.getOldStatus().name() : null)
                .newStatus(history.getNewStatus().name())
                .changedBy(history.getChangedBy())
                .changedAt(history.getCreatedAt())
                .build();
    }
}
