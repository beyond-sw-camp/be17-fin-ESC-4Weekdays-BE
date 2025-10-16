package com.fourweekdays.fourweekdays.vendor.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "거래처 등록 Dto")
public class VendorCreateDto {

    @Schema(description = "거래처 이름", example = "xx공장")
    private String name;
    @Schema(description = "거래처 전화번호", example = "02-1234-5678")
    private String phoneNumber;
    @Schema(description = "거래처 이메일", example = "~~~@gmail.com")
    private String email;
    @Schema(description = "거래처 설명", example = "서울에 있는 화장품 공장")
    private String description;
    @Schema(description = "거래처 주소", example = "서울시 xx구 xx동 123-45")
    private Address address;


    public Vendor toEntity() {
        return Vendor.builder()
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .description(this.description)
                .address(this.address)
                .build();
    }
}
