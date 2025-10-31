package com.fourweekdays.fourweekdays.inventory.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchRequest;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.InventoryReadDto;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.ProductInventoryResponse;
import com.fourweekdays.fourweekdays.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="재고 기능")
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@RestController
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(
            summary = "재고 목록 조회",
            description = "재고의 목록을 카테고리별로 검색해 조회할수 있다"
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Page<ProductInventoryResponse>>> inventoriesByProduct(@RequestParam(defaultValue = "0") int page,
                                                                                             @RequestParam(defaultValue = "10") int size,
                                                                                             @RequestBody InventorySearchRequest request) {
        return ResponseEntity.ok(BaseResponse.success(inventoryService.searchInventoryByProduct(request, page, size)));
    }

    //    @PostMapping
    public ResponseEntity<BaseResponse<Page<InventoryReadDto>>> inventoryList(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestBody InventorySearchRequest request) {
        Page<InventoryReadDto> inventories = inventoryService.searchInventory(request, page, size);
        return ResponseEntity.ok(BaseResponse.success(inventories));
    }

    // 재고 상세 조회
    @Operation(
            summary = "재고 상세 조회",
            description = "특정 재고의 상세 정보를 조회할 수 있다 재고의 상품정보까지 열람 가능"
    )
    @GetMapping("/{productCode}")
    public ResponseEntity<BaseResponse<ProductInventoryResponse>> inventoryRead(@PathVariable String productCode) {
        return ResponseEntity.ok(BaseResponse.success(inventoryService.productInventoryDetail(productCode)));
    }
}
