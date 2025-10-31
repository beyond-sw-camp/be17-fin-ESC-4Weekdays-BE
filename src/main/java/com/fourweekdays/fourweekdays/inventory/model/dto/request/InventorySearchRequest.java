package com.fourweekdays.fourweekdays.inventory.model.dto.request;

import java.time.LocalDate;

public record InventorySearchRequest(
        String inboundCode,

        String productCode,
        String productName,

        String locationCode,

        String inboundManagerName,  // 입고 담당자(입고 요청한 사람)
//        String putawayWorkerName, // 적치 작업자
        LocalDate createdFrom,
        LocalDate createdTo
) {
}