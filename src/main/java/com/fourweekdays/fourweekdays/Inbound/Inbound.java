package com.fourweekdays.fourweekdays.Inbound;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Inbound extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity; // 입고 수량

    @Enumerated(EnumType.STRING)
    private InboundStatus status;
}
