package com.fourweekdays.fourweekdays.asn.model.dto.response;

import com.fourweekdays.fourweekdays.asn.model.entity.Asn;
import com.fourweekdays.fourweekdays.purchaseorder.model.dto.response.PurchaseOrderProductResponseDto;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public class AsnResponse {

    private Long id;
    private String asnCode;
    private String vendorName;
    private LocalDateTime expectedDate;
    private String description; // 비고

    private List<PurchaseOrderProductResponseDto> products;

    public static AsnResponse toDto(Asn asn) {
        PurchaseOrder purchaseOrder = asn.getPurchaseOrder();

        return AsnResponse.builder()
                .id(asn.getId())
                .asnCode(asn.getAsnCode())
                .vendorName(asn.getVendor().getName())
                .expectedDate(asn.getExpectedDate())
                .description(asn.getDescription())
                .products(purchaseOrder.getProducts().stream()
                        .map(PurchaseOrderProductResponseDto::toDto)
                        .toList())
                .build();
    }
}
