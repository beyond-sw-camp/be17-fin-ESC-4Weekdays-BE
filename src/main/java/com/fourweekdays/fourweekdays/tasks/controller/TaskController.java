package com.fourweekdays.fourweekdays.tasks.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskAssignRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.response.TaskDetailResponse;
import com.fourweekdays.fourweekdays.tasks.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@RestController
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/{taskId}/assign")
    public ResponseEntity<Void> assignWorker(@PathVariable Long taskId,
                                             @RequestBody TaskAssignRequest request) {
        taskService.assignWorker(taskId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{taskId}/start")
    public ResponseEntity<BaseResponse<Void>> startTask(@PathVariable Long taskId) {
        taskService.startTask(taskId);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @PostMapping("/{taskId}/complete")
    public ResponseEntity<BaseResponse<Void>> completeTask(@PathVariable Long taskId,
                                                           @Valid @RequestBody TaskCompleteRequest request) {
        taskService.completeTask(taskId, request);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @PostMapping("/{taskId}/cancel")
    public ResponseEntity<BaseResponse<Void>> cancelTask(@PathVariable Long taskId,
                                                         @RequestParam String reason) {
        taskService.cancelTask(taskId, reason);
        return ResponseEntity.ok(BaseResponse.success(null));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<BaseResponse<TaskDetailResponse>> getTaskDetail(@PathVariable Long taskId) {
        TaskDetailResponse response = taskService.getTaskDetail(taskId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
