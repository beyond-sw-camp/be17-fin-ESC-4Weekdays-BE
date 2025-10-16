package com.fourweekdays.fourweekdays.purchaseorder.model.dto.request;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderStatus;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PurchaseOrderCreateDto {

    @Schema(description = "공급업체 선택", example = "Supplier A")
    @NotNull(message = "공급업체를 선택해주세요")
    private Long vendorId; // 공급업체 ID

    @Schema(description = "발주일", example = "2025-10-10")
    @NotNull(message = "발주일을 입력해주세요")
    private LocalDateTime orderDate; // 발주일

    @Schema(description = "입고 예정일", example = "2025-11-11")
    @NotNull(message = "입고 예정일을 입력해주세요")
    private LocalDateTime expectedDate; // 입고 예정일

    @Schema(description = "발주 상품 선택", example = "상품 A")
    @NotEmpty(message = "발주 상품을 선택해주세요.")
    private List<PurchaseOrderItemRequestDto> items;

    @Schema(description = "비고 입력", example = "비고 입력입니다.")
    @Size(max = 1000, message = "비고는 1000자 이내로 입력해주세요")
    private String description;

    // TODO: 다른 엔티티가 정의되면 리팩토링
    public PurchaseOrder toEntity(Vendor vendor) {
        return PurchaseOrder.builder()
                .vendor(vendor)
                .status(PurchaseOrderStatus.REQUESTED)
                .orderDate(this.orderDate)
                .expectedDate(this.expectedDate)
                .description(this.description)
                .build();
    }
}