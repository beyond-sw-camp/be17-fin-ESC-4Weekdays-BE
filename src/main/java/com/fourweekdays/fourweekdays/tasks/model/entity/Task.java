package com.fourweekdays.fourweekdays.tasks.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Task extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status; // PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member worker;

    @Column(name = "reference_id")
    private Long referenceId; // 입고, 출고

    private String note; // 내용 기록

    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @Builder
    public Task(TaskCategory category, TaskStatus status, Long referenceId) {
        this.category = category;
        this.status = status;
        this.referenceId = referenceId;
    }

    // 공통 비즈니스 로직
    public void assignTo(Member worker) {
        if (this.status != TaskStatus.PENDING) {
            throw new TaskException(TASK_CANNOT_ASSIGN);
        }
        this.worker = worker;
        this.status = TaskStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
    }

    public void start() {
        if (this.status != TaskStatus.ASSIGNED) {
            throw new TaskException(TASK_CANNOT_START);
        }
        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(String note) {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new TaskException(TASK_CANNOT_COMPLETE);
        }
        this.note = note;
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel(String note) {
        if (this.status == TaskStatus.COMPLETED) {
            throw new TaskException(TASK_CANNOT_CANCEL);
        }
        this.note = note;
        this.status = TaskStatus.CANCELLED;
    }
}