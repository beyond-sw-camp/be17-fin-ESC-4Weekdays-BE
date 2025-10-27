package com.fourweekdays.fourweekdays.tasks.repository;

import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InspectionTaskRepository extends JpaRepository<InspectionTask, Long> {

    Optional<InspectionTask> findByTaskId(Long taskId);
}
