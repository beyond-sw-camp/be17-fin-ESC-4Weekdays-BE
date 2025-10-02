package com.fourweekdays.fourweekdays.Inbound;

import jakarta.persistence.*;

@Entity
public class Inbound {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity; // 입고 수량

    @Enumerated(EnumType.STRING)
    private InboundStatus status;
}
