package com.fourweekdays.fourweekdays.tasks.service;

import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundStatusUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import com.fourweekdays.fourweekdays.tasks.factory.InboundTaskFactory;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import com.fourweekdays.fourweekdays.tasks.repository.InspectionTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InboundTaskServiceTest {

    @Mock
    private TaskService taskService;

    @Mock
    private InspectionTaskRepository inspectionTaskRepository;

    @Mock
    private InboundTaskFactory inboundTaskFactory;

    @Mock
    private InboundService inboundService;

    @InjectMocks
    private InboundTaskService inboundTaskService;

    @Test
    void 검수_작업을_완료한다() {
        // given
        Long taskId = 1L;
        Long inboundId = 100L;
        TaskCompleteRequest request = new TaskCompleteRequest(taskId, "검수 완료");

        InspectionTask inspectionTask = InspectionTask.builder()
                .inboundId(inboundId)
                .build();

        given(inspectionTaskRepository.findByTaskId(taskId))
                .willReturn(Optional.of(inspectionTask));

        // when
        inboundTaskService.completeInspection(taskId, request);

        // then
        verify(taskService).completeTask(taskId, request);
        verify(inboundService).updateInboundStatus(
                eq(inboundId),
                any(InboundStatusUpdateRequest.class)
        );
        verify(inboundTaskFactory).createPutawayTask(inboundId);
    }

    @Test
    void 검수_작업이_없으면_예외를_발생시킨다() {
        // given
        Long taskId = 1L;
        TaskCompleteRequest request = new TaskCompleteRequest(taskId, "검수 완료");

        given(inspectionTaskRepository.findByTaskId(taskId))
                .willReturn(Optional.empty());

        // when & then
        assertThrows(TaskException.class,
                () -> inboundTaskService.completeInspection(taskId, request));
    }
}