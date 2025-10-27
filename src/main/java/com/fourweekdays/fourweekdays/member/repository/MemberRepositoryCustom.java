package com.fourweekdays.fourweekdays.member.repository;

import com.fourweekdays.fourweekdays.member.model.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findAllWithPaging(Pageable pageable);
}
