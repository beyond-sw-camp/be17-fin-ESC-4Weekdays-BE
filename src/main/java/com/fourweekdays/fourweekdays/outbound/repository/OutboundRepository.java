package com.fourweekdays.fourweekdays.outbound.repository;

import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.outbound.model.entity.Outbound;
import com.fourweekdays.fourweekdays.outbound.model.entity.OutboundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OutboundRepository extends JpaRepository<Outbound, Long>, OutboundRepositoryCustom {

    boolean existsByOrder(Order order);

    @Query("SELECT o FROM Outbound o " +
            "JOIN FETCH o.items i " +
            "JOIN FETCH i.product " +
            "WHERE o.id = :outboundId")
    Optional<Outbound> findByIdWithItemsAndProduct(@Param("outboundId") Long outboundId);

    /**
     * 상태가 expectedStatus 인 경우에만 nextStatus 로 변경.
     * - 반환값 1  : 이번 호출이 "진짜 승인"을 한 경우
     * - 반환값 0  : 이미 다른 트랜잭션이 상태를 바꾼 경우 이후 호출은 아무 일도 안 함
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Outbound o " +
            "SET o.status = :nextStatus " +
            "WHERE o.id = :id AND o.status = :expectedStatus")
    int updateStatusIfMatches(@Param("id") Long id,
                              @Param("expectedStatus") OutboundStatus expectedStatus,
                              @Param("nextStatus") OutboundStatus nextStatus);
}
