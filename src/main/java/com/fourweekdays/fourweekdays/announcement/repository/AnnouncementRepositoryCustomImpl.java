package com.fourweekdays.fourweekdays.announcement.repository;


import com.fourweekdays.fourweekdays.announcement.model.entity.Announcement;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fourweekdays.fourweekdays.announcement.model.entity.QAnnouncement.announcement;

@RequiredArgsConstructor
public class  AnnouncementRepositoryCustomImpl implements AnnouncementRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Announcement> findAllWithPaging(Pageable pageable) {
        List<Announcement> content = queryFactory
                .selectFrom(announcement)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(announcement.pinned.desc(), announcement.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(announcement.count())
                .from(announcement)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}