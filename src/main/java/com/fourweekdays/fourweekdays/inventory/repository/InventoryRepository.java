package com.fourweekdays.fourweekdays.inventory.repository;

import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, InventoryRepositoryCustom {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i " +
            "WHERE i.product.id = :productId " +
            "AND i.location.id = :locationId " +
            "AND (i.lotNumber = :lotNumber)")
    Optional<Inventory> findByProductAndLocationAndLotWithLock(@Param("productId") Long productId,
                                                               @Param("locationId") Long locationId,
                                                               @Param("lotNumber") String lotNumber);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.quantity > 0")
    List<Inventory> findByProductId(@Param("productId") Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.location.id = :locationId AND i.quantity > 0")
    List<Inventory> findByLocationId(@Param("locationId") Long locationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i FROM Inventory i
            WHERE i.product.id = :productId
            AND i.location.id = :locationId
            ORDER BY i.lotNumber ASC
            """)
    List<Inventory> findByProductIdAndLocationIdOrderByLotNumberAscForUpdate(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId
    );

}
