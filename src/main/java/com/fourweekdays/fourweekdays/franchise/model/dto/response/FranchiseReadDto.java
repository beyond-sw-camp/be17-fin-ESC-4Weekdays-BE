package com.fourweekdays.fourweekdays.franchise.model.dto.response;

import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class FranchiseReadDto {
    private Long id;
    private String franchiseCode;
    private String name;
    private String phoneNumber;
    private String email;
    private String description;
    private Address address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FranchiseReadDto from(FranchiseStore entity) {
        return FranchiseReadDto.builder()
                .id(entity.getId())
                .franchiseCode(entity.getFranchiseCode())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .address(entity.getAddress())
                .status(entity.getStatus().toString())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
