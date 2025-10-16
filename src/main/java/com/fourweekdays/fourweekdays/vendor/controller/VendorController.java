package com.fourweekdays.fourweekdays.vendor.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorCreateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorUpdateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.response.VendorReadDto;
import com.fourweekdays.fourweekdays.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    @GetMapping
    public ResponseEntity<BaseResponse<Long>> createVendor(VendorCreateDto dto) {
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
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<VendorReadDto>>> readAllVendors(Integer page, Integer size) {
        List<VendorReadDto> result = vendorService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(
            summary = "거래처 수정",
            description = "거래처의 이름, 전화번호, 이메일, 설명, 상태, 주소를 수정한다"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> updateVendor(@PathVariable Long id, VendorUpdateDto dto) {
        vendorService.update(id, dto);
        return ResponseEntity.ok(BaseResponse.success(id));
    }

    @Operation(
            summary = "",
            description = ""
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteVendor(@PathVariable Long id) {
        vendorService.delete(id);
        return ResponseEntity.ok(BaseResponse.success("Deleted"));
    }
}
