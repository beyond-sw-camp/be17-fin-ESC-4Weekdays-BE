package com.fourweekdays.fourweekdays.inventory.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchDto;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.InventoryListDto;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.InventoryReadDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="재고 기능")
@RestController
@RequestMapping("/api/inventories") // 복수형으로 작성
public class InventoryController {

    // 재고 목록 조회
    @Operation(
            summary = "재고 목록 조회",
            description = "재고의 목록을 카테고리별로 검색해 조회할수 있다"
    )
    @GetMapping
    public ResponseEntity<BaseResponse<InventoryListDto>> inventoryList(InventorySearchDto dto) {
        return ResponseEntity.ok(BaseResponse.success(new InventoryListDto()));
    }

    // 재고 상세 조회
    @Operation(
            summary = "재고 상세 조회",
            description = "특정 재고의 상세 정보를 조회할 수 있다 재고의 상품정보까지 열람 가능"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InventoryReadDto>> inventoryRead(@PathVariable Integer id) {
        return ResponseEntity.ok(BaseResponse.success(new InventoryReadDto()));
    }
}
