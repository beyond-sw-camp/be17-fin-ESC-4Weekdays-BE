package com.fourweekdays.fourweekdays.location.service;


import com.fourweekdays.fourweekdays.location.exception.LocationException;
import com.fourweekdays.fourweekdays.location.model.dto.request.LocationCreateRequest;
import com.fourweekdays.fourweekdays.location.model.dto.response.LocationResponse;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.model.entity.LocationStatus;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fourweekdays.fourweekdays.location.exception.LocationExceptionType.LOCATION_CODE_DUPLICATED;
import static com.fourweekdays.fourweekdays.location.exception.LocationExceptionType.LOCATION_NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public List<LocationResponse> getAvailableLocationsByVendor(Long vendorId, int minCapacity) {
        List<Location> locations = locationRepository.findAvailableLocationsByVendor(vendorId, minCapacity);
        return locations.stream()
                .map(LocationResponse::from)
                .toList();
    }

    public LocationResponse getLocationByCode(String locationCode) {
        Location location = locationRepository.findByLocationCodeWithLock(locationCode)
                .orElseThrow(() -> new LocationException(LOCATION_NOT_FOUND));
        return LocationResponse.from(location);
    }

    public List<LocationResponse> getLocationsByVendor(Long vendorId) {
        return locationRepository.findByVendorId(vendorId).stream()
                .map(LocationResponse::from)
                .toList();
    }

    @Transactional
    public Long createLocation(LocationCreateRequest request) {
        String locationCode = request.zone() + "-" + request.section();

        // TODO: 이것도 동시성 이슈 있지 않나?
        if (locationRepository.existsByLocationCode(locationCode)) {
            throw new LocationException(LOCATION_CODE_DUPLICATED);
        }

        Location location = Location.builder()
                .zone(request.zone())
                .section(request.section())
                .vendorId(request.vendorId())
                .capacity(request.capacity())
                .status(LocationStatus.AVAILABLE)
                .description(request.description())
                .build();

        return locationRepository.save(location).getId();
    }

}