package com.fourweekdays.fourweekdays.inbound.repository;

import com.fourweekdays.fourweekdays.inbound.model.dto.response.InboundReadDto;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InboundRepositoryCustom {

    Page<Inbound> findAllWithPaging(Pageable pageable);

    Page<InboundReadDto> findByProductName(String productName, Pageable pageable);
}
