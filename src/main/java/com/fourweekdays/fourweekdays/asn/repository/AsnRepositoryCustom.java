package com.fourweekdays.fourweekdays.asn.repository;


import com.fourweekdays.fourweekdays.asn.model.entity.Asn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AsnRepositoryCustom {

    Page<Asn> findAllWithPaging(Pageable pageable);
}
