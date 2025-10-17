package com.fourweekdays.fourweekdays.tasks.model.dto.response;

import lombok.Getter;

@Getter
public class TaskReadDto {

    private Long id;
    private String workerName;
    private Long inboundId;
    private String status;
}
