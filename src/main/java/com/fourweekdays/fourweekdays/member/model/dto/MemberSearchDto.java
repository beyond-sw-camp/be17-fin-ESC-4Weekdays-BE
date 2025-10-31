package com.fourweekdays.fourweekdays.member.model.dto;

import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MemberSearchDto {
    private String name;
    private AuthStatus status;
    private MemberRole role;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}
