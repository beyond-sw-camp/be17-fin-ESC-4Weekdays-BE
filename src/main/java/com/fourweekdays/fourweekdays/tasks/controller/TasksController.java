package com.fourweekdays.fourweekdays.tasks.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskAssignDto;
import com.fourweekdays.fourweekdays.tasks.model.dto.response.TaskReadDto;
import com.fourweekdays.fourweekdays.tasks.service.InspectionTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks/inspection")
public class TasksController {

    private final InspectionTaskService inspectionTaskService;

    // 검수 작업자 할당
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> assignWorker(@RequestBody TaskAssignDto dto) {
        Long taskId = inspectionTaskService.assignWorker(dto.getWarehousingId(), dto.getWorkerId());
        return ResponseEntity.ok(BaseResponse.success(taskId));
    }

    // 특정 작업자 작업 목록 조회
    public ResponseEntity<BaseResponse<List<TaskReadDto>>> getTasksByWorker(@PathVariable Long workerId) {
        List<TaskReadDto> taskList = inspectionTaskService.getTasksByWorker(workerId);
        return ResponseEntity.ok(BaseResponse.success(taskList));
    }


}
