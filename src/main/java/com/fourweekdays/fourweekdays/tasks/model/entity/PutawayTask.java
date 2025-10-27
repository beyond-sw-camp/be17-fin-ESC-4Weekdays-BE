package com.fourweekdays.fourweekdays.tasks.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PutawayTask {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long inboundId;

    @OneToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Builder
    public PutawayTask(Long inboundId, Task task) {
        this.inboundId = inboundId;
        this.task = task;
    }
}