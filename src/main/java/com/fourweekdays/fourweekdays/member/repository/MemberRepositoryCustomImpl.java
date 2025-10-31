package com.fourweekdays.fourweekdays.member.repository;

import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.member.querydsl.condition.MemberPredicateBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.fourweekdays.fourweekdays.member.model.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class  MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Member> findAllWithPaging(Pageable pageable) {
        List<Member> content = queryFactory
                .selectFrom(member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Member> searchMembers(
            String name,
            AuthStatus status,
            MemberRole role,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        BooleanBuilder predicate = MemberPredicateBuilder.buildMemberPredicate(
                name, status, role, fromDate, toDate
        );

        // ✅ 실제 데이터
        List<Member> content = queryFactory
                .selectFrom(member)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.joinAt.desc())
                .fetch();

        // ✅ 전체 개수 구하기
        Long total = queryFactory
                .select(member.count())
                .from(member)
                .where(predicate)
                .fetchOne();

        // ✅ 페이징 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }
}