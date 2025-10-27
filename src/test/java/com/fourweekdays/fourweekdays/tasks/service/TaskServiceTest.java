package com.fourweekdays.fourweekdays.tasks.service;

import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskAssignRequest;
import com.fourweekdays.fourweekdays.tasks.model.dto.request.TaskCompleteRequest;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskCategory;
import com.fourweekdays.fourweekdays.tasks.model.entity.TaskStatus;
import com.fourweekdays.fourweekdays.tasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void 작업자를_할당한다() {
        // given
        Long taskId = 1L;
        Long memberId = 10L;
        TaskAssignRequest request = new TaskAssignRequest(memberId, "할당");

        Task task = Task.builder()
                .category(TaskCategory.INSPECTION)
                .status(TaskStatus.PENDING)
                .referenceId(100L)
                .build();

        Member member = Member.builder().name("간술맨").build();

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        taskService.assignWorker(taskId, request);

        // then
        assertEquals(TaskStatus.ASSIGNED, task.getStatus());
        assertEquals(member, task.getWorker());
        assertNotNull(task.getAssignedAt());
        verify(taskRepository).findById(taskId);
        verify(memberRepository).findById(memberId);
    }

    @Test
    void 작업을_시작한다() {
        // given
        Long taskId = 1L;
        Task task = Task.builder()
                .category(TaskCategory.INSPECTION)
                .status(TaskStatus.ASSIGNED)
                .referenceId(100L)
                .build();

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when
        taskService.startTask(taskId);

        // then
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertNotNull(task.getStartedAt());
    }

    @Test
    void 작업을_완료한다() {
        // given
        Long taskId = 1L;
        TaskCompleteRequest request = new TaskCompleteRequest(taskId, "완료");

        Task task = Task.builder()
                .category(TaskCategory.INSPECTION)
                .status(TaskStatus.IN_PROGRESS)
                .referenceId(100L)
                .build();

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when
        taskService.completeTask(taskId, request);

        // then
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals("완료", task.getNote());
        assertNotNull(task.getCompletedAt());
    }

    @Test
    void 대기중이_아닌_작업은_할당할_수_없다() {
        // given
        Task task = Task.builder()
                .category(TaskCategory.INSPECTION)
                .status(TaskStatus.COMPLETED) // 이미 완료
                .build();
        Member member = Member.builder().name("간술맨").build();

        given(memberRepository.findById(10L)).willReturn(Optional.of(member));
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        // when & then
        assertThrows(TaskException.class,
                () -> taskService.assignWorker(1L, new TaskAssignRequest(10L, "")));
    }

    @Test
    void 할당되지_않은_작업은_시작할_수_없다() {
        // given
        Task task = Task.builder()
                .category(TaskCategory.INSPECTION)
                .status(TaskStatus.PENDING)
                .build();

        given(taskRepository.findById(1L)).willReturn(Optional.of(task));

        // when & then
        assertThrows(TaskException.class,
                () -> taskService.startTask(1L));
    }
}