package com.fourweekdays.fourweekdays.outbound.comtroller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.common.BaseResponseStatus;
import com.fourweekdays.fourweekdays.member.model.UserAuth;
import com.fourweekdays.fourweekdays.outbound.model.dto.request.OutboundCreateDto;
import com.fourweekdays.fourweekdays.outbound.model.dto.response.OutboundReadDto;
import com.fourweekdays.fourweekdays.outbound.service.OutboundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outbounds")
public class OutboundController {

    private final OutboundService outboundService;

    // 출고 요청(출고서 등록)
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> require(@RequestBody OutboundCreateDto dto,
                                                      @AuthenticationPrincipal UserAuth manager) {
        Long saveId = outboundService.createOutbound(dto, manager);
        return ResponseEntity.ok(BaseResponse.success(saveId));
    }

    // 출고서 전체 조회
    @GetMapping
    public ResponseEntity<BaseResponse<Page<OutboundReadDto>>> getOutboundList(@RequestParam(defaultValue = "0") int page,
                                                                               @RequestParam(defaultValue = "10") int size) {
        Page<OutboundReadDto> outboundList = outboundService.getOutboundList(page, size);
        return ResponseEntity.ok(BaseResponse.success(outboundList));
    }

    // 출고서 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OutboundReadDto>> getOutboundDetails(@PathVariable Long id) {
        OutboundReadDto outboundDto = outboundService.getOutboundDetails(id);
        if (outboundDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(BaseResponseStatus.OUTBOUND_NOT_FOUND));
        }
        return ResponseEntity.ok(BaseResponse.success(outboundDto));
    }

    // 출고 승인
    @PatchMapping("/{id}/approve")
    public ResponseEntity<BaseResponse<String>> approveOutbound(@PathVariable Long id) {
        outboundService.approveOutbound(id);
        return ResponseEntity.ok(BaseResponse.success("출고 승인 완료"));
    }

//    // 검수 작업
//    @PatchMapping("/{id}/inspection")
//    public ResponseEntity<BaseResponse<String>> inspectionOutbound(@PathVariable Long id, @RequestBody List<OutboundInspectionRequest> requestList) {
//        outboundService.updateInspection(id, requestList);
//        return ResponseEntity.ok(BaseResponse.success("출고 검수 완료"));
//    }

    // 출고 거절
    @PatchMapping("/{id}/cancelled")
    public ResponseEntity<BaseResponse<String>> cancelledOutbound(@PathVariable Long id) {
        outboundService.cancelledOutbound(id);
        return ResponseEntity.ok(BaseResponse.success("출고 작업 취소"));
    }
}
