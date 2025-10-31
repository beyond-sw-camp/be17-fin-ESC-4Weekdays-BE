package com.fourweekdays.fourweekdays.tasks.repository;

import com.fourweekdays.fourweekdays.tasks.model.entity.PutawayTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PutawayTaskRepository extends JpaRepository<PutawayTask, Long> {

    Optional<PutawayTask> findByTaskId(Long taskId);
}
