package com.fourweekdays.fourweekdays.inbound.controller;

import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundCreateRequestDto;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundUpdateRequestDto;
import com.fourweekdays.fourweekdays.inbound.model.dto.response.InboundReadDto;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import com.fourweekdays.fourweekdays.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {
    private final InboundService inboundService;

    @Operation(
            summary = "입고 등록",
            description = "새로운 입고를 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> createInbound(@RequestBody InboundCreateRequestDto dto) {
        Long result = inboundService.create(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 입고 목록 조회
    @Operation(summary = "입고 목록 조회", description = "입고 리스트를 페이지 단위로 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<InboundReadDto>>> listInbound(Integer page, Integer size) {
//        List<InboundReadDto> result = inboundService.list(page, size);
        List<InboundReadDto> mockData = new ArrayList<>();
        return ResponseEntity.ok(BaseResponse.success(mockData));
    }

    // 입고 상세 조회
    @Operation(
            summary = "입고 상세 조회",
            description = "특정 입고의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InboundReadDto>> detailInbound(@PathVariable Long id) {
        InboundReadDto result = inboundService.detail(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 입고 수정
    @Operation(
            summary = "입고 수정",
            description = "기존 입고 정보를 수정합니다."
    )
    @PostMapping("/update")
    public ResponseEntity<BaseResponse<Long>> updateInbound(@RequestBody InboundUpdateRequestDto dto) {
        Long result = inboundService.update(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 입고 삭제
    @Operation(
            summary = "입고 삭제",
            description = "특정 입고를 삭제(soft delete)합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteInbound(@PathVariable Long id) {
        inboundService.softDelete(id);
        return ResponseEntity.ok(BaseResponse.success("success"));
    }
}
