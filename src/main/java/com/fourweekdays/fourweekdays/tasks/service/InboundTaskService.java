package com.fourweekdays.fourweekdays.tasks.service;

import com.fourweekdays.fourweekdays.inbound.exception.InboundException;
import com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundStatusUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import com.fourweekdays.fourweekdays.inventory.service.InventoryService;
import com.fourweekdays.fourweekdays.location.exception.LocationException;
import com.fourweekdays.fourweekdays.location.exception.LocationExceptionType;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.location.service.LocationService;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType;
import com.fourweekdays.fourweekdays.tasks.factory.InboundTaskFactory;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.PutawayCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.PutawayLocationAssignRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskAssignRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import com.fourweekdays.fourweekdays.tasks.model.entity.PutawayTask;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskStatus;
import com.fourweekdays.fourweekdays.tasks.repository.InspectionTaskRepository;
import com.fourweekdays.fourweekdays.tasks.repository.PutawayTaskRepository;
import com.fourweekdays.fourweekdays.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType.INBOUND_NOT_FOUND;
import static com.fourweekdays.fourweekdays.location.exception.LocationExceptionType.LOCATION_NOT_FOUND;
import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class InboundTaskService {

    private final TaskService taskService;
    private final InspectionTaskRepository inspectionTaskRepository;
    private final InboundTaskFactory inboundTaskFactory;
    private final InboundService inboundService;
    private final PutawayTaskRepository putawayTaskRepository;
    private final InboundRepository inboundRepository;
    private final InventoryService inventoryService;
    private final LocationRepository locationRepository;
    private final TaskRepository taskRepository;


    @Transactional
    public void completeInspection(Long taskId, TaskCompleteRequest request) {
        InspectionTask inspectionTask = inspectionTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskException(INSPECTION_TASK_NOT_FOUND));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));

        task.complete(request.note()); // 단일 트랜잭션으로 변경
        // TODO: status 업데이트 구문을 dto가 아닌 방식도 생각해보자.

        log.info(request.note());
        inboundService.updateInboundStatus(inspectionTask.getInboundId(), new InboundStatusUpdateRequest(InboundStatus.PUTAWAY));

        // 검수 완료시에는 적치 위치가 지정되지 않은 적치 작업이 자동으로 생성
        inboundTaskFactory.createPutawayTask(inspectionTask.getInboundId());
    }

    @Transactional
    public void assignLocationAndWorker(Long taskId, PutawayLocationAssignRequest request) {
        // 검수 완료로 생성된 PutawayTask 조회
        // TODO: 이것도 여러 관리자가 동시에 작업 할당을 하는 경우 동시성 이슈가 생길 수 있으나, 현재는 지금 구조로 진행.
        PutawayTask putawayTask = putawayTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskException(PUTAWAY_TASK_NOT_FOUND));

        Task task = putawayTask.getTask();
        if (task.getStatus() != TaskStatus.PENDING) {
            throw new TaskException(TASK_CANNOT_ASSIGN);
        }
        if (putawayTask.isLocationAssigned()) {
            throw new TaskException(TaskExceptionType.PUTAWAY_LOCATION_ALREADY_ASSIGNED);
        }

        Inbound inbound = inboundRepository.findById(putawayTask.getInboundId())
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));

        Long vendorId = inbound.getVendorId(); // vendorId
        int totalQuantity = inbound.getTotalReceivedQuantity(); // 총 수량

        // Location 검증 - Vendor 일치 + 용량 확인
        Location location = locationRepository.findByLocationCodeWithLock(request.locationCode())
                .orElseThrow(() -> new LocationException(LOCATION_NOT_FOUND));
        location.validateForPutaway(vendorId, totalQuantity);

        // TODO: 동시성 이슈?
        putawayTask.assignLocation(request.locationCode()); // 적치 작업에 적치 위치 할당

        // 작업자 할당
        TaskAssignRequest assignRequest = new TaskAssignRequest(
                request.workerId(),
                request.note()
        );
        taskService.assignWorker(taskId, assignRequest);
    }

    @Transactional
    public void completePutawayTask(Long taskId, PutawayCompleteRequest request) {
        // PutawayTask 완료 처리
        PutawayTask putawayTask = putawayTaskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new TaskException(PUTAWAY_TASK_NOT_FOUND));

        TaskCompleteRequest taskCompleteRequest = new TaskCompleteRequest(request.note());
        taskService.completeTask(taskId, taskCompleteRequest);

        // ===== 재고 생성 ===== //
        String locationCode = putawayTask.getAssignedLocationCode();
        inventoryService.createInventoryFromInbound(
                putawayTask.getInboundId(),
                locationCode
        );

        // Inbound 상태 변경: PUTAWAY → COMPLETED
        inboundService.updateInboundStatus(
                putawayTask.getInboundId(),
                new InboundStatusUpdateRequest(InboundStatus.COMPLETED)
        );
    }
}