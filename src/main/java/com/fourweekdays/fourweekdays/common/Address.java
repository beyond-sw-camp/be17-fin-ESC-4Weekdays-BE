package com.fourweekdays.fourweekdays.common;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    private String zipcode; // 우편번호
    private String street; // 도로명 주소
    private String detail; // 상세 주소 (건물명, 호수 등)
    private String city; // 도시
    private String country; // 국가 코드 (예: KR, US)

}