package com.fourweekdays.fourweekdays.tasks.repository;

import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TaskRepositoryCustom {

    Page<Task> findAllWithPaging(Pageable pageable);

}