package com.fourweekdays.fourweekdays.vendor.model.dto.request;

import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VendorStatusUpdateDto {

    @NotNull(message = "상태 값은 필수입니다.")
    private VendorStatus status;

}
