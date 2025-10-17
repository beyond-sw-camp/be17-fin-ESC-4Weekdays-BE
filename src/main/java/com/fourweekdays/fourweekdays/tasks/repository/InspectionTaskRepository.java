package com.fourweekdays.fourweekdays.tasks.repository;

import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.tasks.model.entity.InspectionTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionTaskRepository extends JpaRepository<InspectionTask, Long> {
    List<InspectionTask> findByWorkerId(Long workerId);
    InspectionTask findByInbound(Inbound inbound);
}
