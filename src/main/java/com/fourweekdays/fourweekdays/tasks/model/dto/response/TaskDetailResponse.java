package com.fourweekdays.fourweekdays.tasks.model.dto.response;

import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.tasks.model.entity.*;

import java.time.LocalDateTime;

public record TaskDetailResponse(
        Long taskId,
        TaskCategory category,
        TaskStatus status,
        String workerName,
        String note,
        LocalDateTime assignedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt,

        Long referenceId,
        String referenceCode,
        String assignedLocationCode
        //TODO: 지금은 일단 검수 적치 밖에 없으니 Dto에 추가하는 형식으로 가고 나중에 피킹 포장 만들어지면 각자 응답 쪼개는게 응답 나누는 방식이 좋을듯
) {
    public static TaskDetailResponse ofInspection(Task task, InspectionTask inspectionTask, Inbound inbound) {
        return new TaskDetailResponse(
                task.getId(),
                task.getCategory(),
                task.getStatus(),
                task.getWorker() != null ? task.getWorker().getName() : null,
                task.getNote(),
                task.getAssignedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                inbound.getId(),
                inbound.getInboundCode(),
                null
        );
    }

    public static TaskDetailResponse ofPutaway(Task task, PutawayTask putawayTask, Inbound inbound) {
        return new TaskDetailResponse(
                task.getId(),
                task.getCategory(),
                task.getStatus(),
                task.getWorker() != null ? task.getWorker().getName() : null,
                task.getNote(),
                task.getAssignedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                inbound.getId(),
                inbound.getInboundCode(),
                putawayTask.getAssignedLocationCode()
        );
    }
}
