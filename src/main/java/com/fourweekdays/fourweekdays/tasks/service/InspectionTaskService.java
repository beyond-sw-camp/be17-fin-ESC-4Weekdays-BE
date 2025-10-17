package com.fourweekdays.fourweekdays.tasks.service;

import com.fourweekdays.fourweekdays.common.BaseResponseStatus;
import com.fourweekdays.fourweekdays.common.exception.CustomException;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.tasks.model.dto.response.TaskReadDto;
import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import com.fourweekdays.fourweekdays.tasks.model.TaskStatus;
import com.fourweekdays.fourweekdays.tasks.repository.InspectionTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InspectionTaskService {

    private final InspectionTaskRepository inspectionTaskRepository;
    private final InboundRepository inboundRepository;
    private final WorkerRepository workerRepository;

    // 검수 작업자 할당
    public Long assignWorker(Long inboundId, Long workerId) {
        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.INBOUND_NOT_FOUND));

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.WORKER_NOT_FOUND));

        InspectionTask existingTask = inspectionTaskRepository.findByInbound(inbound);

        if (existingTask != null) {
            switch (existingTask.getStatus()) {
                case WAITING:
                case ASSIGNED:
                    throw new CustomException(BaseResponseStatus.TASK_ALREADY_ASSIGNED);
                case IN_PROGRESS:
                    throw new CustomException(BaseResponseStatus.TASK_ALREADY_IN_PROGRESS);
                case COMPLETED:
                    break;
                default:
                    throw new CustomException(BaseResponseStatus.SERVER_ERROR);
            }
        }

        InspectionTask task = InspectionTask.builder()
                .inbound(inbound)
                .worker(worker)
                .status(TaskStatus.ASSIGNED)
                .build();

        inspectionTaskRepository.save(task);
        return task.getId();
    }

    // 검수 작업 상태 변경
    public void updateStatus(Long taskId, TaskStatus status) {
        InspectionTask task = inspectionTaskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(BaseResponseStatus.TASK_NOT_FOUND));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new CustomException(BaseResponseStatus.TASK_ALREADY_IN_PROGRESS);
        }

        task.updateStatus(status);
    }

    // 특정 작업자 기준 검수 작업 목록 조회
    public List<TaskReadDto> getTasksByWorker(Long workerId) {
        return inspectionTaskRepository.findByWorkerId(workerId)
                .stream()
                .map(TaskReadDto::new)
                .collect(Collectors.toList());
    }
}
