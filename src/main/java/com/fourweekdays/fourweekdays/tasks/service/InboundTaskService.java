package com.fourweekdays.fourweekdays.tasks.service;

import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundStatusUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType;
import com.fourweekdays.fourweekdays.tasks.factory.InboundTaskFactory;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import com.fourweekdays.fourweekdays.tasks.model.entity.PutawayTask;
import com.fourweekdays.fourweekdays.tasks.repository.InspectionTaskRepository;
import com.fourweekdays.fourweekdays.tasks.repository.PutawayTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.INSPECTION_TASK_NOT_FOUND;
import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.PUTAWAY_TASK_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class InboundTaskService {

    private final TaskService taskService;
    private final InspectionTaskRepository inspectionTaskRepository;
    private final InboundTaskFactory inboundTaskFactory;
    private final InboundService inboundService;
    private final PutawayTaskRepository putawayTaskRepository;

    @Transactional
    public void completeInspection(Long taskId, TaskCompleteRequest request) {
        taskService.completeTask(taskId, request);

        InspectionTask inspectionTask = inspectionTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskException(INSPECTION_TASK_NOT_FOUND));

        // TODO: status 업데이트 구문을 dto가 아닌 방식도 생각해보자.
        inboundService.updateInboundStatus(inspectionTask.getInboundId(), new InboundStatusUpdateRequest(InboundStatus.PUTAWAY));

        inboundTaskFactory.createPutawayTask(inspectionTask.getInboundId());
    }

    @Transactional
    public void completePutaway(Long taskId, TaskCompleteRequest request) {
        taskService.completeTask(taskId, request);

        PutawayTask putawayTask = putawayTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskException(PUTAWAY_TASK_NOT_FOUND));

        // TODO: status 업데이트 구문을 dto가 아닌 방식도 생각해보자.
        inboundService.updateInboundStatus(putawayTask.getInboundId(), new InboundStatusUpdateRequest(InboundStatus.COMPLETED));

        // TODO: 적치 작업이 완료되면 재고 생성 트리거가 발생해야 한다.
    }
}