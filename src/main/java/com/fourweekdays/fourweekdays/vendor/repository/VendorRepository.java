package com.fourweekdays.fourweekdays.vendor.repository;

import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long>, VendorRepositoryCustom {

    Optional<Vendor> findByApiKey(String apiKey);
}
