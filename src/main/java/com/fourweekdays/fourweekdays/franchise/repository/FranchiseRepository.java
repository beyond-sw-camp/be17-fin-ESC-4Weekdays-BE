package com.fourweekdays.fourweekdays.franchise.repository;

import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FranchiseRepository extends JpaRepository<FranchiseStore, Long>, FranchiseRepositoryCustom {
    Optional<FranchiseStore> findByApiKey(String apiKey);
}
