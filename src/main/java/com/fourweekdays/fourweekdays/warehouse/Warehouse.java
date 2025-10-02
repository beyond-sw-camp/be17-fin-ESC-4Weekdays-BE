package com.fourweekdays.fourweekdays.warehouse;

import com.fourweekdays.fourweekdays.common.Address;
import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber;
    private String email;

    @Embedded
    private Address address;
}
