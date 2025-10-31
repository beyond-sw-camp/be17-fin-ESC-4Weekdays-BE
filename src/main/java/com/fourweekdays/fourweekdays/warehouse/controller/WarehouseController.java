package com.fourweekdays.fourweekdays.warehouse.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.warehouse.model.dto.request.WarehouseCreateDto;
import com.fourweekdays.fourweekdays.warehouse.model.dto.request.WarehouseUpdateDto;
import com.fourweekdays.fourweekdays.warehouse.model.dto.response.WarehouseReadDto;
import com.fourweekdays.fourweekdays.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> warehouseCreate(@RequestBody WarehouseCreateDto dto) {
        Long result = warehouseService.create(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<WarehouseReadDto>> warehouseRead(@PathVariable Long id) {
        WarehouseReadDto result = warehouseService.read(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<WarehouseReadDto>>> warehouseReads(@RequestParam(defaultValue = "0") Integer page,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        Page<WarehouseReadDto> result = warehouseService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> warehouseUpdate(@RequestBody WarehouseUpdateDto dto, @PathVariable Long id) {
        Long result = warehouseService.update(dto, id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> warehouseDelete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.ok(BaseResponse.success("거래 중단"));
    }
}
