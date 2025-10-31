package com.fourweekdays.fourweekdays.vendor.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "거래처 수정 정보 Dto")
public class VendorUpdateDto {

    @Schema(description = "거래처 이름", example = "xx공장")
    @NotBlank(message = "업체명은 필수입니다")
    @Size(max = 200)
    private String name;

    @Schema(description = "거래처 전화번호", example = "02-8765-4321")
    @Size(max = 20)
    private String phoneNumber;

    @Schema(description = "거래처 이메일", example = "~~~@naver.com")
    @Email
    @Size(max = 100)
    private String email;

    @Schema(description = "거래처 설명", example = "계약 해지한 회사")
    private String description;

    @Schema(description = "거래처 상태", example = "SUSPENDED")
    @NotNull
    private VendorStatus status;

    @Schema(description = "거래처 주소", example = "서울시 xx구 xx동 123-45")
    private Address address;
}

