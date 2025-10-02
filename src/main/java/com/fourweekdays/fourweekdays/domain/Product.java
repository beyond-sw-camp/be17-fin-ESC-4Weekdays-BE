package com.fourweekdays.fourweekdays.domain;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Product extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 ID

    private String productCode; // SKU/바코드
    private String productName; // 상품명

    private int costPrice;      // 매입가 (원가)
    private int listPrice;      // 소비자가 (리스트가)
    private int wholesalePrice; // 도매가 (가맹점 공급가)
    private int marginRate;     // 마진율 (%)

    private String currency; // 금액 단위 (KRW, USD, JPY 등)

    private String specification; // 규격 (예: 500ml, Box 20개입)
    private LocalDateTime expirationAt; // 유통기한
    private String originCountry; // 원산지
//    private String tags; // 태그 (검색 키워드) -> 카테고리 대체 가능성?

//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category; // 상품 카테고리
//
//    @ManyToOne
//    @JoinColumn(name = "partner_id")
//    private Vendor vendor;   // 공급업체 (Vendor)
}
