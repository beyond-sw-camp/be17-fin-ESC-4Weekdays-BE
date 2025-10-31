package com.fourweekdays.fourweekdays.product.model.dto.request;


import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import com.fourweekdays.fourweekdays.product.model.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Schema(description = "상품 수정 요청 DTO")
public class ProductUpdateDto {

    @Schema(description = "상품명", example = "콜드브루 원액 1L")
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 200, message = "상품명은 200자 이하로 입력해주세요.")
    private String name;

    @Schema(description = "상품 단위 (EA, Box, Kg 등)", example = "EA")
    @Size(max = 50, message = "단위는 50자 이하로 입력해주세요.")
    private String unit;

    @Schema(description = "상품 단가 (0원 이상)", example = "4500")
    @NotNull(message = "단가는 필수입니다.")
    @Min(value = 0, message = "단가는 0원 이상이어야 합니다.")
    private Long unitPrice;

    @Schema(description = "상품 설명", example = "비욘드커피 매장에서 사용하는 콜드브루 원액 1리터 제품입니다.")
    @Size(max = 1000, message = "설명은 1000자 이하로 입력해주세요.")
    private String description;

    @Schema(description = "상품 상태 (ACTIVE, INACTIVE, DISCONTINUED)", example = "ACTIVE")
    @NotNull(message = "상품 상태는 필수입니다.")
    private ProductStatus status;

    @Schema(description = "공급업체 ID (Vendor 식별자)", example = "3")
    @NotNull(message = "공급업체는 필수입니다.")
    private Long vendorId; // 공급업체 변경 가능
}
