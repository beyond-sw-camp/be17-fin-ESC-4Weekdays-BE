package com.fourweekdays.fourweekdays.franchise.repository;

import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FranchiseRepositoryCustom {
    Page<FranchiseStore> findAllWithPaging(Pageable pageable);
}
