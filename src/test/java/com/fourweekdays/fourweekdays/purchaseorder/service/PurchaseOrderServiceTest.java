package com.fourweekdays.fourweekdays.purchaseorder.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class PurchaseOrderServiceTest {

    @Test
    @DisplayName("발주서를 생성한다.")
    void create_purchase_order() {

    }

    @Test
    @DisplayName("승인 완료된 발주서는 수정할 수 없다.")
    void cannot_update_purchase_order_after_approval() {

    }
}