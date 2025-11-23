package com.fourweekdays.fourweekdays.tasks.model.dto.response;

import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
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
        String assignedLocationCode,
        String shipper // ShipmentTask용 추가
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
                null,
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
                putawayTask.getAssignedLocationCode(),
                null
        );
    }

    public static TaskDetailResponse ofPicking(Task task, PickingTask pickingTask, Outbound outbound) {
        return new TaskDetailResponse(
                task.getId(),
                task.getCategory(),
                task.getStatus(),
                task.getWorker() != null ? task.getWorker().getName() : null,
                task.getNote(),
                task.getAssignedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                outbound.getId(),
                outbound.getOutboundCode(),
                null,
                null
        );
    }

    public static TaskDetailResponse ofPacking(Task task, PackingTask packingTask, Outbound outbound) {
        return new TaskDetailResponse(
                task.getId(),
                task.getCategory(),
                task.getStatus(),
                task.getWorker() != null ? task.getWorker().getName() : null,
                task.getNote(),
                task.getAssignedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                outbound.getId(),
                outbound.getOutboundCode(),
                null,
                null
        );
    }

    public static TaskDetailResponse ofShipment(Task task, ShipmentTask shipmentTask, Outbound outbound) {
        return new TaskDetailResponse(
                task.getId(),
                task.getCategory(),
                task.getStatus(),
                task.getWorker() != null ? task.getWorker().getName() : null,
                task.getNote(),
                task.getAssignedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                outbound.getId(),
                outbound.getOutboundCode(),
                null,
                shipmentTask.getShipper()
        );
    }
}