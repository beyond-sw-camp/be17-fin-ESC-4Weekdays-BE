package com.fourweekdays.fourweekdays.franchise.model.dto.request;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStatus;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
public class FranchiseCreateDto {
    private String name;
    private String phoneNumber;
    private String email;
    private String description;
    private Address address;

    public FranchiseStore toEntity(String franchiseCode) {
        return FranchiseStore.builder()
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .description(this.description)
                .address(this.address)
                .franchiseCode(franchiseCode)
                .status(FranchiseStatus.ACTIVE)
                .build();
    }
}
