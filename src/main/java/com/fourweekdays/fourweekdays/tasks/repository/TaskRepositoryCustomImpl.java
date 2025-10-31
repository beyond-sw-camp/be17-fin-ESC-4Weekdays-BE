package com.fourweekdays.fourweekdays.tasks.repository;

import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.fourweekdays.fourweekdays.member.model.entity.QMember.member;
import static com.fourweekdays.fourweekdays.tasks.model.entity.QTask.task;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryCustomImpl implements TaskRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Task> findAllWithPaging(Pageable pageable) {
        List<Task> content = queryFactory
                .selectFrom(task)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(task.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(task.count())
                .from(task)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}