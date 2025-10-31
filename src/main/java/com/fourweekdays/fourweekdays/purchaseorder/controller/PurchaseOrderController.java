package com.fourweekdays.fourweekdays.purchaseorder.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.purchaseorder.model.dto.request.PurchaseOrderCreateDto;
import com.fourweekdays.fourweekdays.purchaseorder.model.dto.request.PurchaseOrderUpdateDto;
import com.fourweekdays.fourweekdays.purchaseorder.model.dto.response.PurchaseOrderReadDto;
import com.fourweekdays.fourweekdays.purchaseorder.service.PurchaseOrderService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<BaseResponse<Long>> purchaseRequest(@Valid @RequestBody PurchaseOrderCreateDto requestDto) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.create(requestDto)));
    }

    @Operation(
            summary = "발주 상세 조회",
            description = "발주 ID를 기준으로 해당 발주 상세 정보를 조회한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PurchaseOrderReadDto>> purchaseOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.findByPurchaseOrderId(id)));
    }

    @Operation(
            summary = "발주 목록 조회",
            description = "전체 발주 내역을 조회한다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<Page<PurchaseOrderReadDto>>> purchaseOrderList(@RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.findPurchaseOrderListByPaging(page, size)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> updatePurchaseOrder(@PathVariable Long id,
                                                                  @Valid @RequestBody PurchaseOrderUpdateDto requestDto) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.update(id, requestDto)));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<BaseResponse<Long>> approvePurchaseOrder(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(purchaseOrderService.approve(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> cancelOrder(@PathVariable Long id) {
        purchaseOrderService.cancel(id);
        return ResponseEntity.ok((BaseResponse.success("발주 취소")));
    }
}
