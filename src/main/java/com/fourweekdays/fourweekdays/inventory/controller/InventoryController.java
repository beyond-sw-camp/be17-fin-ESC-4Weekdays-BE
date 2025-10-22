package com.fourweekdays.fourweekdays.inventory.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.inventory.model.dto.request.InventorySearchDto;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.InventoryListDto;
import com.fourweekdays.fourweekdays.inventory.model.dto.response.InventoryReadDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {

    // 재고 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponse<InventoryListDto>> inventoryList(InventorySearchDto dto) {
        return ResponseEntity.ok(BaseResponse.success(new InventoryListDto()));
    }

    // 재고 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InventoryReadDto>> inventoryRead(@PathVariable Integer id) {
        return ResponseEntity.ok(BaseResponse.success(new InventoryReadDto()));
    }
}
