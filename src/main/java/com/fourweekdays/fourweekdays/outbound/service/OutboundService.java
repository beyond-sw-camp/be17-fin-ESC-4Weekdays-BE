package com.fourweekdays.fourweekdays.outbound.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.inventory.exception.InventoryException;
import com.fourweekdays.fourweekdays.inventory.model.entity.Inventory;
import com.fourweekdays.fourweekdays.inventory.repository.InventoryRepository;
import com.fourweekdays.fourweekdays.location.model.entity.Location;
import com.fourweekdays.fourweekdays.location.repository.LocationRepository;
import com.fourweekdays.fourweekdays.member.exception.MemberException;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.order.exception.OrderException;
import com.fourweekdays.fourweekdays.order.model.entity.Order;
import com.fourweekdays.fourweekdays.order.model.entity.OrderStatus;
import com.fourweekdays.fourweekdays.order.repository.OrderRepository;
import com.fourweekdays.fourweekdays.outbound.exception.OutboundException;
import com.fourweekdays.fourweekdays.outbound.model.dto.request.OutboundCreateDto;
import com.fourweekdays.fourweekdays.outbound.model.dto.request.OutboundInspectionRequest;
import com.fourweekdays.fourweekdays.outbound.model.dto.response.OutboundReadDto;
import com.fourweekdays.fourweekdays.outbound.model.entity.*;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundInventoryHistoryRepository;
import com.fourweekdays.fourweekdays.outbound.repository.OutboundRepository;
import com.fourweekdays.fourweekdays.tasks.factory.OutboundTaskFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.fourweekdays.fourweekdays.inventory.exception.InventoryExceptionType.INSUFFICIENT_INVENTORY;
import static com.fourweekdays.fourweekdays.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;
import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.ORDER_CANNOT_APPROVED;
import static com.fourweekdays.fourweekdays.order.exception.OrderExceptionType.ORDER_NOT_FOUND;
import static com.fourweekdays.fourweekdays.outbound.exception.OutboundExceptionType.*;

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
    private final LocationRepository locationRepository;
    private final InventoryRepository inventoryRepository;
    private final OutboundInventoryHistoryRepository outboundHistoryRepository;

    // 재고 감소 또는 제거
    @Transactional
    public void destroyOrDecreaseFromOutbound(Long outboundId) {
        Outbound outbound = chekOutbound(outboundId);

        List<Long> vendorIds = outbound.getItems().stream()
                .map(i -> i.getProduct().getVendor().getId())
                .distinct()
                .toList();

        // TODO vendor이 정해지지 않은 구역도 받게
        List<Location> locations = locationRepository.findAllByVendorIds(vendorIds);

        Map<Long, Location> locationMap = locations.stream()
                .collect(Collectors.toMap(Location::getVendorId, l -> l));

        List<OutboundInventoryHistory> allHistories = new ArrayList<>();

        for (OutboundProductItem item : outbound.getItems()) {
            Long vendorId = item.getProduct().getVendor().getId();
            Location location = locationMap.get(vendorId);
            List<OutboundInventoryHistory> outboundHistoryList =
                    destroyOrDecreaseInventory(item.getProduct().getId(), item.getOrderedQuantity(), location.getId(), outbound);
            allHistories.addAll(outboundHistoryList);
        }
        outboundHistoryRepository.saveAll(allHistories);
    }

    // 출고 생성
// 주문과 완전 동일하게 생성되는 시나리오로 진행하겠음
    @Transactional
    public Long createOutbound(OutboundCreateDto dto) {

        // member와 order 검증
        Order order = verification(dto);

        // Outboun Entity 만들기
        Outbound outbound = createBaseOutbound(dto);

        // order에 상품 목록으로 OutboundProductItems 만들기
        addItemsFromOrder(outbound, order);

        // TODO addDirectItems x 일단 아이템을 추가로 받지 않게 하겠음

        return outboundRepository.save(outbound).getId();
    }

    // 출고 승인
    @Transactional
    public void approveOutbound(Long id) {
        Outbound outbound = chekOutbound(id);
        outbound.updateStatus(OutboundStatus.APPROVED);

        // 출고 작업 생성
        otfTaskFactory.createPickingTask(id);
        destroyOrDecreaseFromOutbound(id);

    }

    // 출고 거절
    @Transactional
    public void cancelledOutbound(Long id) {
        Outbound outbound = chekOutbound(id);

        if (outbound.getStatus() != OutboundStatus.APPROVED && outbound.getStatus() != OutboundStatus.REQUESTED) {
            throw new OutboundException(OUTBOUND_CANNOT_CANCEL);
        }
        outbound.updateStatus(OutboundStatus.CANCELLED);
        // TODO 재고 차감되었던만큼 증가

    }

    // TODO task가 작업 착수 -> 피킹중
    @Transactional
    public void updatePicking(Long id) {
        Outbound outbound = chekOutbound(id);
        outbound.updateStatus(OutboundStatus.PICKING);
    }

    // TODO task가 피킹 완료 -> 검수중
    @Transactional
    public void updatePacking(Long id) {
        Outbound outbound = chekOutbound(id);
        outbound.updateStatus(OutboundStatus.INSPECTION);
    }

    // 검수 완료 작업
    @Transactional
    public void updateInspection(Long id, List<OutboundInspectionRequest> requestList) {
        Outbound outbound = chekOutbound(id);

        if (outbound.getStatus() != OutboundStatus.INSPECTION) {
            throw new OutboundException(OUTBOUND_INVALID_STATUS_FOR_INSPECTION);
        }

        for (OutboundInspectionRequest request : requestList) {
            OutboundProductItem item = outbound.findByItemId(request.getOutboundProductid())
                    .orElseThrow(() -> new OutboundException(OUTBOUND_PRODUCT_NOT_FOUND));
            item.updateInspectionResult(request.getOrderedQuantity());
        }

        // 검수 완료 -> 패킹작업으로 변경
        outbound.updateStatus(OutboundStatus.PACKING);
    }

    // TODO 패킹완료 -> 출하
    @Transactional
    public void updateShipped(Long id) {
        Outbound outbound = chekOutbound(id);
        Order order = orderRepository.findById(outbound.getOrder().getOrderId())
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));
        order.updateShipped();
        outbound.updateStatus(OutboundStatus.SHIPPED);
    }

    // 출고 목록 조회
    public List<OutboundReadDto> getOutboundList() {
        return null;
    }

    // 출고 상세 조회
    public OutboundReadDto getOutboundDetails(Long id) {
        return null;
    }

    private Order verification(OutboundCreateDto dto) {
        memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderException(ORDER_NOT_FOUND));

        if (!order.getStatus().equals(OrderStatus.APPROVED)) {
            throw new OrderException(ORDER_CANNOT_APPROVED);
        }

        if (outboundRepository.existsByOrder(order)) {
            throw new OutboundException(OUTBOUND_ORDER_EXISTENCE);
        }
        return order;
    }

    private Outbound createBaseOutbound(OutboundCreateDto dto) {
        String OutboundCode = codeGenerator.generate(OUTBOUND_CODE_PREFIX);
        OutboundStatus status = OutboundStatus.REQUESTED;

        return dto.toEntity(OutboundCode, status);
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

    private Outbound chekOutbound(Long id) {
        return outboundRepository.findById(id)
                .orElseThrow(() -> new OutboundException(OUTBOUND_NOT_FOUND));
    }

    private List<OutboundInventoryHistory> destroyOrDecreaseInventory(Long productId, Integer orderedQuantity, Long locationId, Outbound outbound) {
        List<Inventory> inventories =
                inventoryRepository.findByProductIdAndLocationIdOrderByLotNumberAscForUpdate(productId, locationId);

        int remaining = orderedQuantity;
        List<OutboundInventoryHistory> histories = new ArrayList<>();

        for (Inventory inventory : inventories) {
            if (remaining <= 0) break;

            Integer currentQty = inventory.getQuantity();
            int deducted = Math.min(currentQty, remaining);

            inventory.decrease(deducted);
            OutboundInventoryHistory history = OutboundInventoryHistory.builder()
                    .outbound(outbound)
                    .inventory(inventory)
                    .lotNumber(inventory.getLotNumber())
                    .quantityChanged(-deducted)
                    .status(OutboundInventoryHistoryStatus.ACTIVE)
                    .build();

            histories.add(history);
            remaining -= deducted;
        }

        if (remaining > 0) {
            throw new InventoryException(INSUFFICIENT_INVENTORY);
        }

        inventoryRepository.saveAll(inventories);

        return histories;
    }
}
