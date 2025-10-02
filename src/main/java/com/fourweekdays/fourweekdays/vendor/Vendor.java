package com.fourweekdays.fourweekdays.vendor;

import com.fourweekdays.fourweekdays.common.Address;
import jakarta.persistence.*;

@Entity
public class Vendor {

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
