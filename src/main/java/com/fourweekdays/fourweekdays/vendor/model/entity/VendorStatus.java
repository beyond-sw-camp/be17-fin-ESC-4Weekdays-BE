package com.fourweekdays.fourweekdays.vendor.model.entity;

import lombok.Getter;

@Getter
public enum VendorStatus {

    ACTIVE("활성", "현재 거래중인 업체입니다."),
    INACTIVE("비활성", "일시적인 이유로 거래가 정지된 업체입니다."),
    SUSPENDED("거래정지", "폐업 등의 이유로 거래가 정지된 업체입니다.");

    private final String displayName;
    private final String description;

    VendorStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
