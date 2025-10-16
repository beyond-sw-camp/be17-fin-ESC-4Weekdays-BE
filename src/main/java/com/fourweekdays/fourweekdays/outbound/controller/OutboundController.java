package com.fourweekdays.fourweekdays.outbound.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.common.BaseResponseStatus;
import com.fourweekdays.fourweekdays.outbound.model.dto.request.OutboundCreateDto;
import com.fourweekdays.fourweekdays.outbound.model.dto.response.OutboundReadDto;
import com.fourweekdays.fourweekdays.outbound.model.dto.response.OutboundStatusResponse;
import com.fourweekdays.fourweekdays.outbound.service.OutboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "출고 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/outbounds")
public class OutboundController {

    private final OutboundService outboundService;

    // 출고 요청(출고서 등록)
    @Operation(
            summary = "출고 요청 등록",
            description = "출고서(Outbound를 신규로 생성한다.)"
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> require(@RequestBody OutboundCreateDto dto) {
        Long saveId = outboundService.createOutbound(dto);
        return ResponseEntity.ok(BaseResponse.success(saveId));
    }

    // 출고 승인
    @Operation(
            summary = "출고 승인",
            description = "출고 요청 상태가 '승인 대기(PENDING)'인 출고서를 승인 처리한다. <br>" +
                    "승인 후 상태는 APPROVED로 변경된다."
    )
    @PostMapping("/{id}/approve")
    public ResponseEntity<BaseResponse<OutboundStatusResponse>> approveOutbound(@PathVariable Long id) {
        OutboundStatusResponse result = outboundService.approveOutbound(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 출고 거절
    @Operation(
            summary = "출고 거절",
            description = "출고 요청 상태가 '승인 대기(PENDING)'인 출고서를 거절 처리한다. <br>" +
                    "거절 후 상태는 REJECTED로 변경된다."
    )
    @PostMapping("/{id}/reject")
    public ResponseEntity<BaseResponse<OutboundStatusResponse>> rejectOutbound(@PathVariable Long id) {
        OutboundStatusResponse result = outboundService.rejectOutbound(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 출고서 전체 조회
    @Operation(
            summary = "출고서 목록 조회",
            description = "등록된 모든 출고서(Outbound) 목록을 조회한다. <br>" +
                    "필요 시 상태(승인 대기, 승인, 거절)에 따라 필터링할 수 있다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<OutboundReadDto>>> getOutboundList() {
        List<OutboundReadDto> outboundList = outboundService.getOutboundList();
        return ResponseEntity.ok(BaseResponse.success(outboundList));
    }

    // 출고서 상세 조회
    @Operation(
            summary = "출고서 상세 조회",
            description = "출고서 ID를 기준으로 상세 정보를 조회한다. <br>" +
                    "출고 수량, 출고 유형, 상태, 상품 및 거래처 정보 등을 반환한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OutboundReadDto>> getOutboundDetails(@PathVariable Long id) {
        OutboundReadDto outboundDto = outboundService.getOutboundDetails(id);
        if (outboundDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(BaseResponseStatus.OUTBOUND_NOT_FOUND));
        }
        return ResponseEntity.ok(BaseResponse.success(outboundDto));
    }
}
