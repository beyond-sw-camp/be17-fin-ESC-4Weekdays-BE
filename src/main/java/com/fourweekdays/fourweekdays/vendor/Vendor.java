package com.fourweekdays.fourweekdays.vendor;

import com.fourweekdays.fourweekdays.common.Address;
import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Vendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessRegistrationNo; // 사업자 등록 번호

    private String name;
    private String phoneNumber;
    private String email;

    @Embedded
    private Address address;

}
