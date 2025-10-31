package com.fourweekdays.fourweekdays.asn.controller;

import com.fourweekdays.fourweekdays.asn.model.dto.response.AsnResponse;
import com.fourweekdays.fourweekdays.asn.service.AsnAdminService;
import com.fourweekdays.fourweekdays.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/admin/asn")
@RequiredArgsConstructor
@RestController
public class AsnAdminController {

    private final AsnAdminService asnAdminService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AsnResponse>> asnDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(asnAdminService.asnDetail(id)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<AsnResponse>>> asnList(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(asnAdminService.asnListByPaging(page, size)));
    }
}
