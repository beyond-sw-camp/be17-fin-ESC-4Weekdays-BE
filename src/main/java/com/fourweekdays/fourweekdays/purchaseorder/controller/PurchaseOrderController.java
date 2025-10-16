package com.fourweekdays.fourweekdays.purchaseorder.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.purchaseorder.model.dto.request.PurchaseOrderCreateDto;
import com.fourweekdays.fourweekdays.purchaseorder.model.dto.response.PurchaseOrderReadDto;
import com.fourweekdays.fourweekdays.purchaseorder.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "발주 기능")
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@RestController
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @Operation(
            summary = "신규 발주 등록",
            description = "신규 발주를 등록한다."
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> purchaseRequest(@RequestBody PurchaseOrderCreateDto requestDto) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.create(requestDto)));
    }

    @Operation(
            summary = "발주 목록 조회",
            description = "전체 발주 내역을 조회한다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<PurchaseOrderReadDto>>> purchaseOrderList() {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.findAll()));
    }

    @Operation(
            summary = "발주 상세 조회",
            description = "발주 ID를 기준으로 해당 발주 상세 정보를 조회한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PurchaseOrderReadDto>> purchaseOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.findByPurchaseOrderId(id)));
    }
}
