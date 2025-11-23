package com.fourweekdays.fourweekdays.tasks.service;

import com.fourweekdays.fourweekdays.inbound.exception.InboundException;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.member.exception.MemberException;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.outbound.exception.OutboundException;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundRepository;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskAssignRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.response.TaskDetailResponse;
import com.fourweekdays.fourweekdays.tasks.model.dto.response.TaskListResponse;
import com.fourweekdays.fourweekdays.tasks.model.entity.*;
import com.fourweekdays.fourweekdays.tasks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType.INBOUND_NOT_FOUND;
import static com.fourweekdays.fourweekdays.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;
import static com.fourweekdays.fourweekdays.outbound.exception.OutboundExceptionType.OUTBOUND_NOT_FOUND;
import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final InspectionTaskRepository inspectionTaskRepository;
    private final MemberRepository memberRepository;
    private final InboundRepository inboundRepository;
    private final PutawayTaskRepository putawayTaskRepository;
    private final PickingTaskRepository pickingTaskRepository;
    private final PackingTaskRepository packingTaskRepository;
    private final ShipmentTaskRepository shipmentTaskRepository;
    private final OutboundRepository outboundRepository;

    @Transactional
    public void assignWorker(Long taskId, TaskAssignRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));

        Member worker = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        task.assignTo(worker);
    }

    @Transactional
    public void startTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));

        task.start();
    }

    @Transactional
    public void completeTask(Long taskId, TaskCompleteRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));

        task.complete(request.note());
    }

    @Transactional
    public void cancelTask(Long taskId, String reason) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));

        task.cancel(reason);
    }

    public TaskDetailResponse getTaskDetail(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));

        return switch (task.getCategory()) {
            case INSPECTION -> inspectionTaskDetail(task);
            case PUTAWAY -> putawayTaskDetail(task);
            case PICKING -> pickingTaskDetail(task);
            case PACKING -> packingTaskDetail(task);
            case SHIPMENT -> shipmentTaskDetail(task);
        };
    }

    private TaskDetailResponse inspectionTaskDetail(Task task) {
        InspectionTask inspectionTask = inspectionTaskRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new TaskException(INSPECTION_TASK_NOT_FOUND));

        Inbound inbound = inboundRepository.findById(inspectionTask.getInboundId())
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));

        return TaskDetailResponse.ofInspection(task, inspectionTask, inbound);
    }

    private TaskDetailResponse putawayTaskDetail(Task task) {
        PutawayTask putawayTask = putawayTaskRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new TaskException(PUTAWAY_TASK_NOT_FOUND));

        Inbound inbound = inboundRepository.findById(putawayTask.getInboundId())
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));

        return TaskDetailResponse.ofPutaway(task, putawayTask, inbound);
    }

    private TaskDetailResponse pickingTaskDetail(Task task) {
        PickingTask pickingTask = pickingTaskRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new TaskException(PICKING_TASK_NOT_FOUND));

        Outbound outbound = outboundRepository.findById(pickingTask.getOutboundId())
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        return TaskDetailResponse.ofPicking(task, pickingTask, outbound);
    }

    private TaskDetailResponse packingTaskDetail(Task task) {
        PackingTask packingTask = packingTaskRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new TaskException(PACKING_TASK_NOT_FOUND));

        Outbound outbound = outboundRepository.findById(packingTask.getOutboundId())
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        return TaskDetailResponse.ofPacking(task, packingTask, outbound);
    }

    private TaskDetailResponse shipmentTaskDetail(Task task) {
        ShipmentTask shipmentTask = shipmentTaskRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new TaskException(SHIPMENT_TASK_NOT_FOUND));

        Outbound outbound = outboundRepository.findById(shipmentTask.getOutboundId())
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        return TaskDetailResponse.ofShipment(task, shipmentTask, outbound);
    }

    //TODO : 지금 서비스단에서 조회하고 하는데 나중에 TaskRepositoryCustomImpl 여기서 fetch join으로 조회하게 수정.
    public Page<TaskListResponse> readAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = taskRepository.findAllWithPaging(pageable);

        return tasks.map(task -> {
            String inboundSummary = null;

            if (task.getCategory() == TaskCategory.INSPECTION || task.getCategory() == TaskCategory.PUTAWAY) {
                inboundSummary = inboundRepository.findById(task.getReferenceId())
                        .map(inbound -> {
                            String vendorName = inbound.getPurchaseOrder().getVendor().getName();
                            List<InboundProduct> products = inbound.getProducts();

                            if (products.isEmpty()) return vendorName;

                            String firstProductName = products.get(0).getProduct().getName();
                            return (products.size() > 1)
                                    ? String.format("%s — %s 외 %d건", vendorName, firstProductName, products.size() - 1)
                                    : String.format("%s — %s", vendorName, firstProductName);
                        })
                        .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));
            }

            return TaskListResponse.from(task, inboundSummary);
        });
    }
}
