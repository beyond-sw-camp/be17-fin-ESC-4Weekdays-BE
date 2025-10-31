package com.fourweekdays.fourweekdays.tasks.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.PutawayCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.PutawayLocationAssignRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.service.InboundTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/inbound-tasks")
@RequiredArgsConstructor
@RestController
public class InboundTaskController {

    private final InboundTaskService inboundTaskService;

    @PostMapping("/inspection/{taskId}/complete")
    public ResponseEntity<BaseResponse<String>> completeInspection(@PathVariable Long taskId, @RequestBody TaskCompleteRequest request) {;
        inboundTaskService.completeInspection(taskId, request);
        return ResponseEntity.ok(BaseResponse.success("검수 완료"));
    }

    @PostMapping("/putaway/{taskId}/assign-location")
    public ResponseEntity<BaseResponse<String>> assignLocationAndWorker(@PathVariable Long taskId,
                                                                        @Valid @RequestBody PutawayLocationAssignRequest request) {
        inboundTaskService.assignLocationAndWorker(taskId, request);
        return ResponseEntity.ok(BaseResponse.success("적치 위치 및 작업자 할당 완료"));
    }

    @PostMapping("/putaway/{taskId}/complete")
    public ResponseEntity<BaseResponse<String>> completePutaway(@PathVariable Long taskId,
                                                                @Valid @RequestBody PutawayCompleteRequest request) {
        inboundTaskService.completePutawayTask(taskId, request);
        return ResponseEntity.ok(BaseResponse.success("적치 완료 및 재고 생성"));
    }
}
