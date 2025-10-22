package com.fourweekdays.fourweekdays.inventory.model.entity;

import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Table(name = "location",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"zone", "row", "level"})
        })
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String zone; // 존 (예: A, B, C)

    @Column(nullable = false, length = 10)
    private String row; // 열 (예: 01, 02)

    @Column(nullable = false, length = 10)
    private String level; // 층 (예: 01, 02, 03)

    @Column(nullable = false, unique = true, length = 50)
    private String code;    // ex) "A-01-03"

    @Column(nullable = false, length = 100)
    private String name;    // ex) "A존 1열 3층"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;  // 해당 존의 브랜드 소유자 (존 단위로 동일)

    @Column(nullable = false)
    private Boolean active = true;

    @Builder
    private Location(String zone, String row, String level, String code, String name, Vendor vendor, Boolean active) {
        this.zone = zone;
        this.row = row;
        this.level = level;
        this.code = code;
        this.name = name;
        this.vendor = vendor;
        this.active = active;
    }

    public static Location create(String zone, String row, String level, Vendor vendor) {
        String code = generateCode(zone, row, level);
        String name = generateName(zone, row, level);

        return Location.builder()
                .zone(zone)
                .row(row)
                .level(level)
                .code(code)
                .name(name)
                .vendor(vendor)
                .active(true)
                .build();
    }

    public static String generateCode(String zone, String row, String level) {
        return String.format("%s-%s-%s", zone, row, level);
    }

    public static String generateName(String zone, String row, String level) {
        return String.format("%s존 %s열 %s층", zone, Integer.parseInt(row), Integer.parseInt(level));
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }
}
