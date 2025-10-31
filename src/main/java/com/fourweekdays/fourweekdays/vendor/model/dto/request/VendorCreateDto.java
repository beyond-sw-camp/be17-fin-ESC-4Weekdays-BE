package com.fourweekdays.fourweekdays.vendor.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor
@Schema(description = "거래처 등록 Dto")
public class VendorCreateDto {

    @Schema(description = "거래처 이름", example = "xx공장")
    @NotNull(message = "업체명을 입력해주세요.")
    private String name;

    @Schema(description = "거래처 전화번호", example = "02-1234-5678")
    private String phoneNumber;
    @Schema(description = "거래처 이메일", example = "~~~@gmail.com")
    private String email;
    @Schema(description = "거래처 설명", example = "서울에 있는 화장품 공장")
    private String description;
    @Schema(description = "거래처 주소", example = "서울시 xx구 xx동 123-45")
    private Address address;

    public Vendor toEntity(String vendorCode) {
        return Vendor.builder()
                .name(this.name)
                .vendorCode(vendorCode)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .description(this.description)
                .address(this.address)
                .status(VendorStatus.ACTIVE) // 기본값: ACTIVE
                .build();
    }
}
