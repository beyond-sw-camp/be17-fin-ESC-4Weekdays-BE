package com.fourweekdays.fourweekdays.location.repository;

import com.fourweekdays.fourweekdays.location.model.entity.Location;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Location l WHERE l.locationCode = :locationCode")
    Optional<Location> findByLocationCodeWithLock(@Param("locationCode") String locationCode);

    boolean existsByLocationCode(String locationCode);

    List<Location> findByVendorId(Long vendorId);

    @Query("SELECT l FROM Location l " +
            "WHERE l.status = 'AVAILABLE' " +
            "AND (l.vendorId = :vendorId OR l.vendorId IS NULL) " +
            "AND (l.capacity - l.usedCapacity) >= :minCapacity " +
            "ORDER BY l.usedCapacity ASC")
    List<Location> findAvailableLocationsByVendor(@Param("vendorId") Long vendorId, @Param("minCapacity") int minCapacity);

    // LocationRepository
    @Query("SELECT l FROM Location l WHERE l.vendorId IN :vendorIds")
    List<Location> findAllByVendorIds(@Param("vendorIds") List<Long> vendorIds);

}
