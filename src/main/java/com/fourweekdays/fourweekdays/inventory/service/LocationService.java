package com.fourweekdays.fourweekdays.inventory.service;

import com.fourweekdays.fourweekdays.inventory.model.entity.Location;
import com.fourweekdays.fourweekdays.inventory.repository.LocationRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    // Vendor의 Location이 있으면 반환, 없으면 할당
    @Transactional
    public Location getLocationForVendor(Vendor vendor) {
        return locationRepository.findByVendorAndActiveTrue(vendor)
                .orElseGet(() -> assignNewLocation(vendor));
    }

    // Vendor에 할당된 활성 Location 조회
    @Transactional(readOnly = true)
    public Location getAssignedLocation(Vendor vendor) {
        return locationRepository.findByVendorAndActiveTrue(vendor)
                .orElseThrow(() -> new IllegalStateException(
                        "Vendor에 할당된 Location이 없습니다: " + vendor.getId()
                ));
    }

     // 새 Location 할당 (자동/수동 정책은 여기서 분기)
    private Location assignNewLocation(Vendor vendor) {
        Location location = findAvailableZone();
        location.assignVendor(vendor);
        return locationRepository.save(location);
    }

    private Location findAvailableZone() {
        return locationRepository.findByVendorIsNullAndActiveTrue()
                .orElseThrow(() -> new IllegalStateException("창고 꽉차서 놓을데 없음"));
    }
}
