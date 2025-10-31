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

    @Column(length = 20)
    private String assignedLocationCode; // 적치 위치는 관리자가 작업 할당시 지정

    @Builder
    public PutawayTask(Long inboundId, Task task) {
        this.inboundId = inboundId;
        this.task = task;
        this.assignedLocationCode = null;
    }

    // ===== 비즈니스 로직 ===== //
    public void assignLocation(String locationCode) {
        this.assignedLocationCode = locationCode;
    }


    public boolean isLocationAssigned() {
        return this.assignedLocationCode != null;
    }
}