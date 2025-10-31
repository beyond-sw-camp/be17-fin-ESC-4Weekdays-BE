package com.fourweekdays.fourweekdays.vendor.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Vendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Long id;

    @Column(unique = true, length = 50, nullable = false)
    private String vendorCode; // 공급업체 코드 (V-001, V-002 등)

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String email;

    @Column(unique = true)
    private String apiKey;

    private String description; // 업체 설명 및 비고

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VendorStatus status; // ACTIVE, INACTIVE, SUSPEND

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY) // TODO: think CASCADE
    private List<Product> productList;

    @Builder
    public Vendor(String vendorCode, String name, String phoneNumber, String email, String apiKey, String description, VendorStatus status, Address address) {
        this.vendorCode = vendorCode;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.description = description;
        this.status = status;
        this.address = address;
        this.productList = new ArrayList<>();
        this.apiKey = generateApiKey();
    }

    // ===== 비즈니스 로직 ===== //
    public void update(String name, String phoneNumber, String email,
                       String description, Address address) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (email != null) this.email = email;
        if (description != null) this.description = description;
        if (address != null) this.address = address;
    }

    public void changeStatus(VendorStatus status) {
        if (status != null) this.status = status;
    }

//    public void suspended() {
//        this.status = VendorStatus.SUSPENDED;
//    }
//
//    public boolean canOrder() {
//        return this.status == VendorStatus.ACTIVE;
//    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }
}
