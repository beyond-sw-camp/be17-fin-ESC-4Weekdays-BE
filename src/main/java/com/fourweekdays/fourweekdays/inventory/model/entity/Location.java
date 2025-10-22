package com.fourweekdays.fourweekdays.inventory.model.entity;

import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(nullable = false)
    private boolean active; //

    // TODO: Location 확장시 LocationDetail 합성 - row, level, capacity, usedCapacity 등

    @Builder
    public Location(int zone) {
        this.zone = zone;
        this.vendor = null;
        this.active = true;
    }

//    public static Location assignToVendor(Integer zone, Vendor vendor) {
//        Location location = new Location();
//        location.zone = zone;
//        location.vendor = vendor;
//        location.active = true;
//        return location;
//    }

    public void assignVendor(Vendor vendor) {
        this.vendor = vendor;
        this.active = true;
    }

    public void unassignVendor() {
        this.vendor = null;
        this.active = false;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

//    public static String generateCode(String zone, String row, String level) {
//        return String.format("%s-%s-%s", zone, row, level);
//    }
//
//    public static String generateName(String zone, String row, String level) {
//        return String.format("%s존 %s열 %s층", zone, Integer.parseInt(row), Integer.parseInt(level));
//    }
}
