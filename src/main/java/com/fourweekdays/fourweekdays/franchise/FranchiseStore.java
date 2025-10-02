package com.fourweekdays.fourweekdays.franchise;

import com.fourweekdays.fourweekdays.common.Address;
import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class FranchiseStore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessRegistrationNo; // 사업자 번호

    private String name;
    private String phoneNumber;
    private String email;

    @Embedded
    private Address address; // 주소

    @Enumerated(EnumType.STRING)
    private StoreStatus status; // (ACTIVE / INACTIVE)
}