package com.fourweekdays.fourweekdays.asn.model.entity;

import lombok.Getter;

@Getter
public enum AsnStatus {
    ACCEPTED("ASN 승인"),
    REJECTED("ASN 거절");

    private final String description;

    AsnStatus(String description) {
        this.description = description;
    }
}
