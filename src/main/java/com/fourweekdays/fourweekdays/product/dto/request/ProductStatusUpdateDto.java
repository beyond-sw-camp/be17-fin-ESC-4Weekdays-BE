package com.fourweekdays.fourweekdays.product.dto.request;

import com.fourweekdays.fourweekdays.product.model.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "상품 상태 변경 요청 DTO")
public class ProductStatusUpdateDto {

    @Schema(description = "변경할 상품 상태 (ACTIVE, INACTIVE, DISCONTINUED)", example = "INACTIVE")
    private ProductStatus status;

    @Schema(description = "상품 상태를 변경한 사용자명 또는 관리자 ID", example = "admin001")
    private String changedBy;
}
