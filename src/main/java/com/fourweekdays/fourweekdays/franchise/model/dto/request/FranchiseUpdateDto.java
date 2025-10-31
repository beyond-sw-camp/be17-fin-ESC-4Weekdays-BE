package com.fourweekdays.fourweekdays.franchise.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FranchiseUpdateDto {
    private String name;
    private String phoneNumber;
    private String email;
    private String description;
    private Address address;
    private FranchiseStatus status;
}
