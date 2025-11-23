package com.fourweekdays.fourweekdays.inbound.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundCreateRequestDto;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundInspectionUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundSearchRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundStatusUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.response.InboundReadDto;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> createInbound(@RequestBody InboundCreateRequestDto dto) {
        Long result = inboundService.create(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<InboundReadDto>> detailInbound(@PathVariable Long id) {
        InboundReadDto result = inboundService.findById(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<Page<InboundReadDto>>> searchInboundWithProduct(@RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size,
                                                                                       @RequestBody InboundSearchRequest request) {
        return ResponseEntity.ok(BaseResponse.success(inboundService.searchInbounds(page, size, request)));
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
        log.info("ㅆ:발");
        inboundService.updateInspection(id, requestList);
        return ResponseEntity.ok(BaseResponse.success("검수 완료"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> cancelInbound(@PathVariable Long id) {
        inboundService.cancel(id);
        return ResponseEntity.ok(BaseResponse.success("입고 취소"));
    }
}
