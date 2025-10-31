package com.fourweekdays.fourweekdays.member.querydsl.condition;

import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.member.model.entity.QMember;
import com.querydsl.core.BooleanBuilder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberPredicateBuilder {

    private static final QMember member = QMember.member;

    public static BooleanBuilder buildMemberPredicate(
            String name,
            AuthStatus status,
            MemberRole role,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(name))
            builder.and(member.name.containsIgnoreCase(name));

        if (status != null)
            builder.and(member.status.eq(status));

        if (role != null)
            builder.and(member.role.eq(role));

        if (fromDate != null)
            builder.and(member.joinAt.goe(fromDate.atStartOfDay()));

        if (toDate != null)
            builder.and(member.joinAt.loe(toDate.atTime(23, 59, 59)));

        return builder;

    }
}
