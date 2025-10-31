package com.fourweekdays.fourweekdays.inbound.repository;

import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InboundRepositoryCustom {

    Page<Inbound> findAllWithPaging(Pageable pageable);

    List<Inbound> searchInboundWithProduct(
            String inboundCode,
            String managerName,
            String productName,
            List<Long> vendorIds
    );
}
