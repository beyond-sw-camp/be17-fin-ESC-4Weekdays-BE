package com.fourweekdays.fourweekdays.warehouse.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import lombok.Getter;

@Getter
public class WarehouseUpdateDto {
    private String name;
    private String phoneNumber;
    private String email;
    private Address address;
}
