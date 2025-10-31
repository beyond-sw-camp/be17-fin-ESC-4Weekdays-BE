package com.fourweekdays.fourweekdays.warehouse.model.dto.response;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.warehouse.model.entity.Warehouse;
import lombok.*;

@Getter
@Builder
public class WarehouseReadDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private Address address;

    public static WarehouseReadDto from(Warehouse entity) {
        return WarehouseReadDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .build();
    };
}
