package com.fourweekdays.fourweekdays.asn.model.dto.response;

import lombok.Builder;

@Builder
public record AsnReceiveResponse(String asnCode, String message) {
}
