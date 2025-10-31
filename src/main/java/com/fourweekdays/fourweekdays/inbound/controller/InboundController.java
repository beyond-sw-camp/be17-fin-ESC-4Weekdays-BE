package com.fourweekdays.fourweekdays.inbound.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundCreateRequestDto;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundInspectionUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundStatusUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.response.InboundReadDto;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "입고 기능")
@RestController
@RequestMapping("/api/inbounds")
@RequiredArgsConstructor
public class InboundController {

    private final InboundService inboundService;

    // TODO: 입고는 발주서 승인시 자동 트리거로 생성된다.
    // TODO: 입고는 수정할 수 없다.
    // TODO: 발주 이후 배송이 완료되어 임시 창고에 입하되면 입고 작업을 할당할 수 있는 상태가 된다.
    // TODO: 작업자는 할당된 작업에 나와있는 발주서를 통한 입고서로(or 작업 지시서) 검수 작업을 수행한다.
    // TODO: 검수 작업이후 적치 예정과 같은 상태로 변경된 입고는 이후 적치 작업으로 할당된다.
    // TODO: 작업자는 위치에 맞게 적치하고 완료 트리거를 통해 재고가 된다.
    // 어제 얘기한 플로우 대로 한 번 적어봄

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
        InboundReadDto result = inboundService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InboundReadDto>> searchInbounds(
            @RequestParam(required = false) String inboundCode,
            @RequestParam(required = false) String managerName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) List<Long> vendorIds
    ) {

        return ResponseEntity.ok(inboundService.searchInbounds(inboundCode, managerName, productName, vendorIds));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<InboundReadDto>>> listByPaging(@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(inboundService.inboundList(page, size)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BaseResponse<String>> updateInbound(@RequestBody InboundStatusUpdateRequest requestDto, @PathVariable Long id) {
        inboundService.updateInboundStatus(id, requestDto);
        return ResponseEntity.ok(BaseResponse.success(requestDto.status().name()));
    }

    @PatchMapping("/{id}/arrive")
    public ResponseEntity<BaseResponse<String>> arriveDelivery(@PathVariable Long id) {
        inboundService.arriveDelivery(id);
        return ResponseEntity.ok(BaseResponse.success("차량 도착함"));
    }

    @PatchMapping("/{id}/inspection")
    public ResponseEntity<BaseResponse<String>> updateInspection(@PathVariable Long id, @RequestBody List<InboundInspectionUpdateRequest> requestList) {
        inboundService.updateInspection(id, requestList);
        return ResponseEntity.ok(BaseResponse.success("검수 완료"));
    }

    // 입고 삭제
    @Operation(
            summary = "입고 삭제",
            description = "특정 입고를 삭제(soft delete)합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> cancelInbound(@PathVariable Long id) {
        inboundService.cancel(id);
        return ResponseEntity.ok(BaseResponse.success("입고 취소"));
    }
}
