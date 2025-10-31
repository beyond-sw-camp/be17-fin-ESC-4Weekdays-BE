package com.fourweekdays.fourweekdays.inbound.querydsl.condition;

import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.model.entity.QInbound;
import com.querydsl.core.BooleanBuilder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class InboundPredicateBuilder {

    private static final QInbound inbound = QInbound.inbound;

    public static BooleanBuilder buildInboundPredicate(
            String inboundCode,
            InboundStatus status,
            String managerName,
            LocalDateTime fromDate,
            LocalDateTime toDate
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(inboundCode))
            builder.and(inbound.inboundCode.containsIgnoreCase(inboundCode));

        if (status != null)
            builder.and(inbound.status.eq(status));

        if (StringUtils.hasText(managerName))
            builder.and(inbound.managerName.containsIgnoreCase(managerName));

        if (fromDate != null)
            builder.and(inbound.scheduledDate.goe(fromDate));

        if (toDate != null)
            builder.and(inbound.scheduledDate.loe(toDate));

        return builder;

    }
}
