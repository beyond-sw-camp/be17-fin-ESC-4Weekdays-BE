package com.fourweekdays.fourweekdays.inventory.repository;

import com.fourweekdays.fourweekdays.inventory.model.entity.Location;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByZone(Integer zone);
    Optional<Location> findByVendor(Vendor vendor);

    Optional<Location> findByVendorAndActiveTrue(Vendor vendor);

    Optional<Location> findByVendorIsNullAndActiveTrue();
}
