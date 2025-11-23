package com.fourweekdays.fourweekdays.outbound.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryException;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.member.exception.MemberException;
import com.fourweekdays.fourweekdays.member.model.UserAuth;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.order.exception.OrderException;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.model.entity.OrderStatus;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import com.fourweekdays.fourweekdays.order.service.OrderAdminService;
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
    private final OrderAdminService orderAdminService;
    private final OrderRepository orderRepository;
    private final OutboundTaskFactory otfTaskFactory;
    private final InventoryRepository inventoryRepository;
    private final OutboundInventoryHistoryRepository outboundHistoryRepository;
    private final TaskRepository taskRepository;
    private final RedissonClient redissonClient;

    // 출고 생성
    @Transactional
    public Long createOutbound(OutboundCreateDto dto, UserAuth manager) {
        Member member = memberRepository.findById(manager.getId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Order order = orderRepository.findByOrderCode(dto.getOrderCode())
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        if (!order.getStatus().equals(OrderStatus.REQUESTED)) {
            throw new OrderException(ORDER_CANNOT_APPROVED);
        }

        if (outboundRepository.existsByOrder(order)) {
            throw new OutboundException(OUTBOUND_ORDER_EXISTENCE);
        }

        Outbound outbound = createBaseOutbound(dto, member, order);
        addItemsFromOrder(outbound, order);

        orderAdminService.approveOrder(order.getOrderId());

        return outboundRepository.save(outbound).getId();
    }

    // 출고 승인
    @Transactional
    public void approveOutbound(Long id) {
        Outbound outbound = checkOutbound(id);
        outbound.updateStatus(OutboundStatus.APPROVED);
        Long taskId = otfTaskFactory.createPickingTask(id);
        destroyOrDecreaseFromOutbound(id, taskId);
    }

    // 출고 취소
    @Transactional
    public void cancelledOutbound(Long id) {
        Outbound outbound = checkOutbound(id);

        if (outbound.getStatus() != OutboundStatus.APPROVED && outbound.getStatus() != OutboundStatus.REQUESTED) {
            throw new OutboundException(OUTBOUND_CANNOT_CANCEL);
        }
        outbound.updateStatus(OutboundStatus.CANCELLED);

        List<OutboundInventoryHistory> histories =
                outboundHistoryRepository.findAllByOutboundIdWithInventory(id);

        validateAllHistoriesArePending(histories);

        recoverInventoryFromOutboundHistory(histories);
    }

    // 피킹 완료
    @Transactional
    public void updatePicking(Long id) {
        Outbound outbound = checkOutbound(id);
        outbound.updateStatus(OutboundStatus.PICKING);
    }

    // 패킹 완료
    @Transactional
    public void updatePacking(Long id) {
        Outbound outbound = checkOutbound(id);
        outbound.updateStatus(OutboundStatus.PACKING);
    }

    //
//    // 검수 완료 작업
//    @Transactional
//    public void updateInspection(Long id, List<OutboundInspectionRequest> requestList) {
//        Outbound outbound = checkOutbound(id);
//
//        if (outbound.getStatus() != OutboundStatus.INSPECTION) {
//            throw new OutboundException(OUTBOUND_INVALID_STATUS_FOR_INSPECTION);
//        }
//
//        for (OutboundInspectionRequest request : requestList) {
//            OutboundProductItem item = outbound.findByItemId(request.getOutboundProductid())
//                    .orElseThrow(() -> new OutboundException(OUTBOUND_PRODUCT_NOT_FOUND));
//            item.updateInspectionResult(request.getOrderedQuantity());
//        }
//
//        // 검수 완료 -> 패킹작업으로 변경
//        outbound.updateStatus(OutboundStatus.PACKING);
//    }
//
    // 출하 완료시 호출 메소드 Order에서 사용할 예정 아니면 상태 하나더 만들던가
    @Transactional
    public void updateShipped(Long id) {
        Outbound outbound = checkOutbound(id);
        Order order = orderRepository.findById(outbound.getOrder().getOrderId())
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        order.updateShipped();
        outbound.updateStatus(OutboundStatus.SHIPPED);
    }

    // 출고 목록 조회
    public Page<OutboundReadDto> getOutboundList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Outbound> outbound = outboundRepository.findAllWithPaging(pageable);
        return outbound.map(OutboundReadDto::from);
    }

    // 출고 상세 조회
    public OutboundReadDto getOutboundDetails(Long id) {
        Outbound outbound = outboundRepository.findById(id)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));
        return OutboundReadDto.from(outbound);
    }

    // 재고 감소 로직
    private void destroyOrDecreaseFromOutbound(Long outboundId, Long taskId) {
        List<OutboundInventoryHistory> allHistories = new ArrayList<>();

        Outbound outbound = outboundRepository.findByIdWithItemsAndProduct(outboundId)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));

        if (outbound.getItems().isEmpty()) {
            throw new OutboundException(OUTBOUND_PRODUCT_NOT_FOUND);
        }
        List<OutboundProductItem> items = outbound.getItems();

        for (OutboundProductItem item : outbound.getItems()) {
            // 재고 감소전 product 단위 락 획득
            String productLockKey = String.format("inventory:decrease:lock:%d", item.getProduct().getId());
            RLock productLock = redissonClient.getLock(productLockKey);

            try {
                boolean locked = productLock.tryLock(10, 5, TimeUnit.SECONDS);
                if (!locked) {
                    throw new InventoryException(LOCK_ACQUISITION_FAILED);
                }

                List<OutboundInventoryHistory> histories =
                        decreaseInventoryByFIFO(
                                item.getProduct().getId(),
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

    // 재고 감소 로직 내부
    private List<OutboundInventoryHistory> decreaseInventoryByFIFO(Long productId, Integer requiredQuantity, Outbound outbound, Long taskId) {
        List<OutboundInventoryHistory> histories = new ArrayList<>();
        Integer remainingQuantity = requiredQuantity;

        // pessimistic_write로 row-level 보호
        List<Inventory> inventories = inventoryRepository
                .findAllByProductIdOrderByLotNumberAsc(productId);
        if (inventories.isEmpty()) {
            throw new InventoryException(INVENTORY_NOT_FOUND);
        }

        for (Inventory inventory : inventories) {
            if (remainingQuantity <= 0) break;
            if (inventory.getQuantity() <= 0) continue;

            String locationLockKey = String.format("location:lock:%d", inventory.getLocation().getId());
            RLock locationLock = redissonClient.getLock(locationLockKey);

            // Location 단위 락 획득
            try {
                boolean locked = locationLock.tryLock(10, 5, TimeUnit.SECONDS);
                if (!locked) {
                    throw new InventoryException(LOCK_ACQUISITION_FAILED);
                }

                int decreaseAmount = Math.min(inventory.getQuantity(), remainingQuantity);

            // 재고 감소
            inventory.decrease(decreaseAmount);
            // 용량 감소
            inventory.getLocation().decreaseUsedCapacity(decreaseAmount);

            histories.add(
                    OutboundInventoryHistory.builder()
                            .outbound(outbound)
                            .inventory(inventory)
                            .product(inventory.getProduct())
                            .location(inventory.getLocation())
                            .quantityChanged(decreaseAmount)
                            .lotNumber(inventory.getLotNumber())
                            .taskId(taskId)
                            .status(OutboundInventoryHistoryStatus.PENDING)
                            .build()
            );

            remainingQuantity -= decreaseAmount;

            } catch (InterruptedException e) {
                throw new InventoryException(LOCK_INTERRUPTED);
            } finally {
                if (locationLock.isHeldByCurrentThread()) {
                    locationLock.unlock();
                }
            }
//            // TODO 재고 소프트 딜리트 구현 -> 재고 0 = 소프트 딜리트
//
//            int decreaseAmount = Math.min(currentStock, remainingQuantity);
//            inventory.decrease(decreaseAmount);
//            OutboundInventoryHistory history = OutboundInventoryHistory.builder()
//                    .outbound(outbound)
//                    .inventory(inventory)
//                    .product(inventory.getProduct())
//                    .location(inventory.getLocation())
//                    .quantityChanged(decreaseAmount)
//                    .lotNumber(inventory.getLotNumber())
//                    .taskId(taskId)
//                    .status(OutboundInventoryHistoryStatus.PENDING)
//                    .build();
//            histories.add(history);
//            remainingQuantity -= decreaseAmount;
        }

        if (remainingQuantity > 0) {
            throw new InventoryException(INVENTORY_NOT_FOUND);
        }

        return histories;
    }

    // 재고 증가 로직
    private void recoverInventoryFromOutboundHistory(List<OutboundInventoryHistory> histories) {
        if (!histories.isEmpty()) {
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
    }

    // 검증 로직들

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

    private Outbound createBaseOutbound(OutboundCreateDto dto, Member manager, Order order) {
        String OutboundCode = codeGenerator.generate(OUTBOUND_CODE_PREFIX);
        OutboundStatus status = OutboundStatus.REQUESTED;

        return dto.toEntity(OutboundCode, status, manager, order);
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
