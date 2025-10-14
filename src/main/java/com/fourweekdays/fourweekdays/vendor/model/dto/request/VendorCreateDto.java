package com.fourweekdays.fourweekdays.vendor.model.dto.request;

import com.fourweekdays.fourweekdays.common.Address;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VendorCreateDto {
    private String businessRegistrationNo;
    private String name;
    private String phoneNumber;
    private String email;
    private String zipcode; // 우편번호
    private String street; // 도로명 주소
    private String detail; // 상세 주소 (건물명, 호수 등)
    private String city; // 도시
    private String country; // 국가 코드 (예: KR, US)

    public Vendor toEntity() {
        Address address = Address.builder()
                .zipcode(this.zipcode)
                .street(this.street)
                .detail(this.detail)
                .city(this.city)
                .country(this.country)
                .build();

        return Vendor.builder()
                .businessRegistrationNo(this.businessRegistrationNo)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .address(address)
                .build();
    }
}
