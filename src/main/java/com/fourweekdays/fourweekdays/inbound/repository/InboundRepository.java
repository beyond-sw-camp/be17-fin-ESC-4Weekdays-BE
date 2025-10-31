package com.fourweekdays.fourweekdays.inbound.repository;

import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundRepository extends JpaRepository<Inbound, Long>, InboundRepositoryCustom {
}
