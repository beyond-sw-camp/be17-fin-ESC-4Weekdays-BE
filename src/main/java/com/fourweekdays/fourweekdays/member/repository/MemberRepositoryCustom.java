package com.fourweekdays.fourweekdays.member.repository;

import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepositoryCustom {

    Page<Member> findAllWithPaging(Pageable pageable);

    Page<Member> searchMembers(
            String name,
            AuthStatus status,
            MemberRole role,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    );
}