package com.fourweekdays.fourweekdays.asn.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asn extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String asnCode; // ASN 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    private LocalDateTime expectedDate; // 도착 날짜

    private String description; // 비고


    public static Asn create(Vendor vendor, PurchaseOrder purchaseOrder,
                             String asnCode, LocalDateTime expectedDate, String description) {
        Asn asn = new Asn();
        asn.vendor = vendor;
        asn.purchaseOrder = purchaseOrder;
        asn.asnCode = asnCode;
        asn.expectedDate = expectedDate;
        asn.description = description;
        return asn;
    }

}