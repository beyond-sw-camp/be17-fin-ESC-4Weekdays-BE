package com.fourweekdays.fourweekdays.location.model.dto.response;

import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;

public record LocationResponse(
        Long id,
        String zone,
        String section,
        String locationCode,
        Long vendorId,
        Integer capacity,
        Integer currentUsage,
        Integer remainingCapacity,
        LocationStatus status,
        String description
) {
    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getZone(),
                location.getSection(),
                location.getLocationCode(),
                location.getVendorId(),
                location.getCapacity(),
                location.getUsedCapacity(),
                location.freeCapacity(),
                location.getStatus(),
                location.getDescription()
        );
    }
}