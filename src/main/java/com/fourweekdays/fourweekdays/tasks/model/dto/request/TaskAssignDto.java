package com.fourweekdays.fourweekdays.tasks.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import org.aspectj.bridge.IMessage;

@Getter
public class TaskAssignDto {

    @NotNull(message = "입고서 ID는 필수입니다.")
    private Long warehousingId;

    @NotNull(message = "작업자 ID는 필수입니다.")
    private Long workerId;

}
