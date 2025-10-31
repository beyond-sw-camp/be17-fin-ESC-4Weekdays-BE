package com.fourweekdays.fourweekdays.location.model.entity;


import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.location.exception.LocationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.fourweekdays.fourweekdays.location.exception.LocationExceptionType.*;


@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Location extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String zone; // 01, 02, 03...

    @Column(nullable = false, length = 10)
    private String section; // A, B, C...

    @Column(nullable = false, unique = true, length = 20)
    private String locationCode; // 01-A, 01-B, 02-A...

    private Long vendorId;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer usedCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationStatus status; // AVAILABLE, CLOSED

    @Column(length = 500)
    private String description; // 비고

    @Builder
    public Location(String zone, String section, Long vendorId, Integer capacity,
                    LocationStatus status, String description) {
        this.zone = zone;
        this.section = section;
        this.locationCode = zone + "-" + section;
        this.vendorId = vendorId;
        this.capacity = capacity != null ? capacity : 15000;
        this.usedCapacity = 0;
        this.status = status != null ? status : LocationStatus.AVAILABLE;
        this.description = description;
    }

    // ===== 비즈니스 로직 ===== //
    public boolean canLoad(int quantity) {
        return this.usedCapacity + quantity <= this.capacity;
    }

    public void increaseUsedCapacity(int quantity) {
        if (!canLoad(quantity)) {
            throw new LocationException(CAPACITY_EXCEEDED);
        }
        this.usedCapacity += quantity;
    }

    public void decreaseUsedCapacity(int quantity) {
        if (this.usedCapacity < quantity) {
            throw new LocationException(USED_CAPACITY_NEGATIVE);
        }
        this.usedCapacity -= quantity;
    }

    public boolean isAvailable() {
        return this.status == LocationStatus.AVAILABLE;
    }

    public int freeCapacity() {
        return this.capacity - this.usedCapacity;
    }

    public boolean isAssignedToVendor(Long vendorId) {
        return this.vendorId == null || this.vendorId.equals(vendorId);
    }

    public void validateForPutaway(Long vendorId, int quantity) {
        if (!isAvailable()) {
            throw new LocationException(LOCATION_NOT_AVAILABLE);
        }
        if (!isAssignedToVendor(vendorId)) {
            throw new LocationException(LOCATION_VENDOR_MISMATCH);
        }
        if (!canLoad(quantity)) {
            throw new LocationException(CAPACITY_EXCEEDED);
        }
    }
}