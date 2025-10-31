package com.fourweekdays.fourweekdays.vendor.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorCreateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorStatusUpdateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorUpdateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.response.VendorReadDto;
import com.fourweekdays.fourweekdays.vendor.service.VendorService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "거래처 기능")
@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @Operation(
            summary = "거래처 등록",
            description = "거래처의 이름, 전화번호, 이메일, 설명, 주소를 등록한다."
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> createVendor(@Valid @RequestBody VendorCreateDto dto) {
        Long result = vendorService.create(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(
            summary = "거래처 상세 조회",
            description = "거래처 코드, 이름, 전화번호, 이메일, 설명, 상태, 주소, 공급 상품 수, 생성 시간, 업데이트 시간을 보여준다"
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<VendorReadDto>> readVendor(@PathVariable Long id) {
        VendorReadDto result = vendorService.read(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(
            summary = "거래처 목록 조회",
            description = "거래처 목록을 페이지로 나눠 조회한다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<Page<VendorReadDto>>> readVendors(@RequestParam(defaultValue = "0") Integer page,
                                                                         @RequestParam(defaultValue = "10") Integer size) {
        Page<VendorReadDto> result = vendorService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 내용 수정
    @Operation(
            summary = "거래처 수정",
            description = "거래처의 이름, 전화번호, 이메일, 설명, 상태, 주소를 수정한다"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> updateVendor(@PathVariable Long id,
                                                           @Valid @RequestBody VendorUpdateDto dto) {
        vendorService.update(id, dto);
        return ResponseEntity.ok(BaseResponse.success(id));
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<Long>> updateVendorStatus(@PathVariable Long id,
                                                                 @Valid @RequestBody VendorStatusUpdateDto dto) {
        vendorService.updateStatus(id, dto.getStatus());
        return ResponseEntity.ok(BaseResponse.success(id));
    }

    @Operation(
            summary = "거레처 삭제",
            description = "거래처를 삭제한다"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> suspendVendor(@PathVariable Long id) {
        vendorService.suspend(id);
        return ResponseEntity.ok(BaseResponse.success("거래 중단"));
    }
}
