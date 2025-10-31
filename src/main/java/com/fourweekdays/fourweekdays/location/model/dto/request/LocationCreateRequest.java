package com.fourweekdays.fourweekdays.location.model.dto.request;

public record LocationCreateRequest(String zone, String section, Long vendorId, Integer capacity, String description) {
}
