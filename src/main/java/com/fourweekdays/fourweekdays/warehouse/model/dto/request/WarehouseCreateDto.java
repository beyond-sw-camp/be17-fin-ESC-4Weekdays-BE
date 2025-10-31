package com.fourweekdays.fourweekdays.warehouse.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.warehouse.model.entity.Warehouse;
import lombok.*;

@Getter
@Builder
public class WarehouseCreateDto {
    private String name;
    private String phoneNumber;
    private String email;
    private Address address;

    public Warehouse toEntity() {
        return Warehouse.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .address(address)
                .build();
    }
}
