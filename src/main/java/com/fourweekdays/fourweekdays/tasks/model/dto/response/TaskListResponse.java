package com.fourweekdays.fourweekdays.tasks.model.dto.response;

import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskCategory;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskStatus;

import java.time.LocalDateTime;


public record TaskListResponse(
        Long id,
        Long referenceId,
        String inboundSummary,
        TaskCategory category,
        TaskStatus status,
        String workerName,
        LocalDateTime assignedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt) {

    public static TaskListResponse from(Task task, String inboundSummary) {
        return new TaskListResponse(
                task.getId(),
                task.getReferenceId(),
                inboundSummary,
                task.getCategory(),
                task.getStatus(),
                task.getWorker() == null ? null : task.getWorker().getName(),
                task.getAssignedAt(),
                task.getStartedAt(),
                task.getCompletedAt()
        );
    }
}
