package com.fourweekdays.fourweekdays.outbound.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryException;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.member.exception.MemberException;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.order.exception.OrderException;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.model.entity.OrderStatus;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import com.fourweekdays.fourweekdays.outbound.exception.OutboundException;
import com.fourweekdays.fourweekdays.outbound.model.dto.request.OutboundCreateDto;
import com.fourweekdays.fourweekdays.outbound.model.dto.response.OutboundReadDto;
import com.fourweekdays.fourweekdays.outbound.model.entity.*;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundInventoryHistoryRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundRepository;
import com.fourweekdays.fourweekdays.tasks.exception.TaskException;
import com.fourweekdays.fourweekdays.tasks.factory.OutboundTaskFactory;
import com.fourweekdays.fourweekdays.tasks.model.entity.Task;
import com.fourweekdays.fourweekdays.tasks.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType.*;
import static com.fourweekdays.fourweekdays.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;
import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.ORDER_CANNOT_APPROVED;
import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.ORDER_NOT_FOUND;
import static com.fourweekdays.fourweekdays.outbound.exception.OutboundExceptionType.*;
import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.OUTBOUND_HISTORY_ALREADY_PROCESSED;
import static com.fourweekdays.fourweekdays.tasks.exception.TaskExceptionType.TASK_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class OutboundService {

    private static final String OUTBOUND_CODE_PREFIX = "OB";

    private final MemberRepository memberRepository;
    private final OutboundRepository outboundRepository;
    private final CodeGenerator codeGenerator;
    private final OrderRepository orderRepository;
    private final OutboundTaskFactory otfTaskFactory;
    private final InventoryRepository inventoryRepository;
    private final OutboundInventoryHistoryRepository outboundHistoryRepository;
    private final TaskRepository taskRepository;
    private final RedissonClient redissonClient;

    // 출고 생성
    @Transactional
    public Long createOutbound(OutboundCreateDto dto, Long managerId) {
        Member manager = memberRepository.findById(managerId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        if (!order.getStatus().equals(OrderStatus.APPROVED)) {
            throw new OrderException(ORDER_CANNOT_APPROVED);
        }

        if (outboundRepository.existsByOrder(order)) {
            throw new OutboundException(OUTBOUND_ORDER_EXISTENCE);
        }

        Outbound outbound = createBaseOutbound(dto, manager);
        addItemsFromOrder(outbound, order);

        return outboundRepository.save(outbound).getId();
    }

    // 출고 승인 (동시성 제어)
    /**
     * 한 Outbound 에 대해 승인 로직은 "실제 재고 차감"이 단 한 번만 일어나도록 설계.
     * - 분산락: 같은 OutboundId 에 대해 동시에 approve 로직이 들어오지 않도록 Redisson 사용
     * - DB 업데이트: status = REQUESTED 인 경우에만 APPROVED 로 변경
     *   -> update 카운트가 0이면 이미 다른 스레드가 승인 완료한 상태이므로 조용히 return
     */
    @Transactional
    public boolean approveOutbound(Long outboundId) {

        String lockKey = "outbound:approve:" + outboundId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean locked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!locked) {
                return false;
            }

            int updatedRows = outboundRepository.updateStatusIfMatches(
                    outboundId,
                    OutboundStatus.REQUESTED,
                    OutboundStatus.APPROVED
            );

            // 최초 승인자만 true
            if (updatedRows == 0) {
                return false;
            }

            Outbound outbound = outboundRepository.findByIdWithItemsAndProduct(outboundId)
                    .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

            destroyOrDecreaseFromOutbound(outbound, null);

            return true;

        } catch (InterruptedException e) {
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    // 출고 취소
    @Transactional
    public void cancelledOutbound(Long id) {
        Outbound outbound = checkOutbound(id);

        if (outbound.getStatus() != OutboundStatus.APPROVED &&
                outbound.getStatus() != OutboundStatus.REQUESTED) {
            throw new OutboundException(OUTBOUND_CANNOT_CANCEL);
        }
        outbound.updateStatus(OutboundStatus.CANCELLED);

        List<OutboundInventoryHistory> histories =
                outboundHistoryRepository.findAllByOutboundIdWithInventory(id);

        validateAllHistoriesArePending(histories);

        recoverInventoryFromOutboundHistory(histories);
    }

    // 상태 변경
    @Transactional
    public void updatePicking(Long id) {
        Outbound outbound = checkOutbound(id);
        outbound.updateStatus(OutboundStatus.PICKING);
    }

    @Transactional
    public void updatePacking(Long id) {
        Outbound outbound = checkOutbound(id);
        outbound.updateStatus(OutboundStatus.PACKING);
    }

    @Transactional
    public void updateShipped(Long id) {
        Outbound outbound = checkOutbound(id);
        Order order = orderRepository.findById(outbound.getOrder().getOrderId())
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        order.updateShipped();
        outbound.updateStatus(OutboundStatus.SHIPPED);
    }

    // 조회
    public Page<OutboundReadDto> getOutboundList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Outbound> outbound = outboundRepository.findAllWithPaging(pageable);
        return outbound.map(OutboundReadDto::from);
    }

    public OutboundReadDto getOutboundDetails(Long id) {
        Outbound outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));
        return OutboundReadDto.from(outbound);
    }

    // 재고 감소 (FIFO)
    private void destroyOrDecreaseFromOutbound(Outbound outbound, Long taskId) {
        List<OutboundInventoryHistory> allHistories = new ArrayList<>();

        if (outbound.getItems().isEmpty()) {
            throw new OutboundException(OUTBOUND_PRODUCT_NOT_FOUND);
        }

        for (OutboundProductItem item : outbound.getItems()) {

            Long productId = item.getProduct().getId();
            String productLockKey = String.format("inventory:decrease:lock:%d", productId);
            RLock productLock = redissonClient.getLock(productLockKey);

            try {
                boolean locked = productLock.tryLock(10, 5, TimeUnit.SECONDS);
                if (!locked) {
                    throw new InventoryException(LOCK_ACQUISITION_FAILED);
                }

                List<OutboundInventoryHistory> histories =
                        decreaseInventoryByFIFO(
                                productId,
                                item.getOrderedQuantity(),
                                outbound,
                                taskId
                        );

                allHistories.addAll(histories);

            } catch (InterruptedException e) {
                throw new InventoryException(LOCK_INTERRUPTED);
            } finally {
                if (productLock.isHeldByCurrentThread()) {
                    productLock.unlock();
                }
            }
        }

        if (!allHistories.isEmpty()) {
            outboundHistoryRepository.saveAll(allHistories);
        }
    }

    private List<OutboundInventoryHistory> decreaseInventoryByFIFO(
            Long productId,
            Integer requiredQuantity,
            Outbound outbound,
            Long taskId
    ) {
        List<OutboundInventoryHistory> histories = new ArrayList<>();
        int remainingQuantity = requiredQuantity;

        List<Inventory> inventories =
                inventoryRepository.findAllByProductIdOrderByLotNumberAsc(productId);

        if (inventories.isEmpty()) {
            throw new InventoryException(INVENTORY_NOT_FOUND);
        }

        for (Inventory inventory : inventories) {
            if (remainingQuantity <= 0) break;
            if (inventory.getQuantity() <= 0) continue;

            String locationLockKey = String.format("location:lock:%d", inventory.getLocation().getId());
            RLock locationLock = redissonClient.getLock(locationLockKey);

            try {
                boolean locked = locationLock.tryLock(10, 5, TimeUnit.SECONDS);
                if (!locked) {
                    throw new InventoryException(LOCK_ACQUISITION_FAILED);
                }

                int decreaseAmount = Math.min(inventory.getQuantity(), remainingQuantity);

                // 재고 감소
                inventory.decrease(decreaseAmount);
                // 로케이션 사용량 감소
                inventory.getLocation().decreaseUsedCapacity(decreaseAmount);

                OutboundInventoryHistory history = OutboundInventoryHistory.builder()
                        .outbound(outbound)
                        .inventory(inventory)
                        .product(inventory.getProduct())
                        .location(inventory.getLocation())
                        .quantityChanged(decreaseAmount)
                        .lotNumber(inventory.getLotNumber())
                        .taskId(taskId) //
                        .status(OutboundInventoryHistoryStatus.PENDING)
                        .build();

                histories.add(history);
                remainingQuantity -= decreaseAmount;

            } catch (InterruptedException e) {
                throw new InventoryException(LOCK_INTERRUPTED);
            } finally {
                if (locationLock.isHeldByCurrentThread()) {
                    locationLock.unlock();
                }
            }
        }

        if (remainingQuantity > 0) {
            throw new InventoryException(INVENTORY_NOT_FOUND);
        }

        return histories;
    }

    // 재고 복구
    private void recoverInventoryFromOutboundHistory(List<OutboundInventoryHistory> histories) {
        if (histories.isEmpty()) {
            return;
        }

        for (OutboundInventoryHistory history : histories) {
            Inventory inventory = history.getInventory();
            inventory.increase(history.getQuantityChanged());
            history.cancel();
        }

        Long taskId = histories.get(0).getTaskId();
        if (taskId != null) {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskException(TASK_NOT_FOUND));
            task.cancel("출고서 취소로 인한 작업 취소");
        }
    }

    // 검증 / 헬퍼
    private void validateAllHistoriesArePending(List<OutboundInventoryHistory> histories) {
        if (histories.isEmpty()) {
            return;
        }
        boolean hasNonPendingHistory = histories.stream()
                .anyMatch(h -> h.getStatus() != OutboundInventoryHistoryStatus.PENDING);

        if (hasNonPendingHistory) {
            throw new OutboundException(OUTBOUND_HISTORY_ALREADY_PROCESSED);
        }
    }

    private Outbound createBaseOutbound(OutboundCreateDto dto, Member manager) {
        String outboundCode = codeGenerator.generate(OUTBOUND_CODE_PREFIX);
        OutboundStatus status = OutboundStatus.REQUESTED;
        return dto.toEntity(outboundCode, status, manager);
    }

    private void addItemsFromOrder(Outbound outbound, Order order) {
        order.getItems().forEach(opItem -> {
            OutboundProductItem outboundProductItem = OutboundProductItem.builder()
                    .product(opItem.getProduct())
                    .orderProductItem(opItem)
                    .orderedQuantity(opItem.getOrderedQuantity())
                    .description(opItem.getDescription())
                    .build();

            outboundProductItem.assignOutbound(outbound);
        });
    }

    private Outbound checkOutbound(Long id) {
        return outboundRepository.findById(id)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));
    }
}
