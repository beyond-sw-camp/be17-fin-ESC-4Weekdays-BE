package com.fourweekdays.fourweekdays.outbound.repository;

import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundInventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundInventoryHistoryRepository extends JpaRepository<OutboundInventoryHistory, Long> {
}
