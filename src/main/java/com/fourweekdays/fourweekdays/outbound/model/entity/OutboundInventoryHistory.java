package com.fourweekdays.fourweekdays.outbound.model.entity;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboundInventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Outbound outbound;

    @ManyToOne(fetch = FetchType.LAZY)
    private Inventory inventory;

    private int quantityChanged;
    private String lotNumber;

    @Enumerated(EnumType.STRING)
    private OutboundInventoryHistoryStatus status;
}
