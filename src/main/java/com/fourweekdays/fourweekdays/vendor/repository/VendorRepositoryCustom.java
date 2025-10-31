package com.fourweekdays.fourweekdays.vendor.repository;

import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VendorRepositoryCustom {

    Page<Vendor> findAllWithPaging(Pageable pageable);
}
