package com.fourweekdays.fourweekdays.tasks.factory;

import com.fourweekdays.fourweekdays.outbound.exception.OutboundException;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundRepository;
import com.fourweekdays.fourweekdays.outbound.service.OutboundService;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskCategory;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskStatus;
import com.fourweekdays.fourweekdays.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.outbound.exception.OutboundExceptionType.OUTBOUND_NOT_FOUND;

@RequiredArgsConstructor
@Component
public class OutboundTaskFactory {

    private final TaskRepository taskRepository;
    private final OutboundRepository outboundRepository;
    private final OutboundService outboundService;

    @Transactional
    public Long createPickingTask(Long outboundId) {
        Outbound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        Task task = createTask(TaskCategory.PICKING, outboundId);
        // PickingTask를 만들어야 할 이유를 모르겠음
        // 조회할때 Outbound -> OutboundProductItem -> Product -> Vendor -> Location
        // 나중에 필요하다 생각되면 PickingTask를 만들어서 거기에 저장해두고 불러오게
        // 검증도 마찬가지 Outbound -> OutboundProductItem -> Product -> Vendor -> Location
        // 비효율적인것 같긴함

        // TODO 제고 감소
        outboundService.destroyOrDecreaseFromOutbound(outboundId);
        return task.getId();
    }

    @Transactional
    public Long createInspectionTask(Long outboundId) {
        Outbound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        Task task = createTask(TaskCategory.INSPECTION, outboundId);
        return task.getId();
    }

    @Transactional
    public Long createPackingTask(Long outboundId) {
        Outbound outbound = outboundRepository.findById(outboundId)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        Task task = createTask(TaskCategory.PACKING, outboundId);
        return task.getId();
    }

    private Task createTask(TaskCategory taskCategory, Long outboundId) {
        Task task = Task.builder()
                .category(taskCategory)
                .status(TaskStatus.PENDING)
                .referenceId(outboundId)
                .build();
        return taskRepository.save(task);
    }
}
