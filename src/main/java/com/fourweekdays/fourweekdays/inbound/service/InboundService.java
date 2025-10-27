package com.fourweekdays.fourweekdays.inbound.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.inbound.exception.InboundException;
import com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundCreateRequestDto;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundInspectionUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundProductDto;
import com.fourweekdays.fourweekdays.inbound.model.dto.request.InboundStatusUpdateRequest;
import com.fourweekdays.fourweekdays.inbound.model.dto.response.InboundReadDto;
import com.fourweekdays.fourweekdays.inbound.model.entity.Inbound;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundProduct;
import com.fourweekdays.fourweekdays.inbound.model.entity.InboundStatus;
import com.fourweekdays.fourweekdays.inbound.repository.InboundRepository;
import com.fourweekdays.fourweekdays.member.exception.MemberException;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import com.fourweekdays.fourweekdays.product.exception.ProductException;
import com.fourweekdays.fourweekdays.product.model.entity.Product;
import com.fourweekdays.fourweekdays.product.repository.ProductRepository;
import com.fourweekdays.fourweekdays.purchaseorder.exception.PurchaseOrderException;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.purchaseorder.repository.PurchaseOrderRepository;
import com.fourweekdays.fourweekdays.tasks.factory.InboundTaskFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType.INBOUND_INVALID_STATUS_FOR_INSPECTION;
import static com.fourweekdays.fourweekdays.inbound.exception.InboundExceptionType.INBOUND_NOT_FOUND;
import static com.fourweekdays.fourweekdays.member.exception.MemberExceptionType.MEMBER_NOT_FOUND;
import static com.fourweekdays.fourweekdays.product.exception.ProductExceptionType.PRODUCT_NOT_FOUND;
import static com.fourweekdays.fourweekdays.purchaseorder.exception.PurchaseOrderExceptionType.PURCHASE_ORDER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundService {

    public static final String INBOUND_CODE_PREFIX = "IB";

    private final MemberRepository memberRepository;
    private final InboundRepository inboundRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;
    private final CodeGenerator codeGenerator;
    private final InboundTaskFactory inboundTaskFactory;

    public Long createByPurchaseOrder(PurchaseOrder purchaseOrder) {
        /**
         * -> 발주 트리거에 의해 트랜잭션 전파 Transactional 어노테이션 없음.
         * TODO: 1. member 연결하고 담당자 배정해야함.
         * TODO: 2. ASN 구현시 발주 승인 트리거가 아닌 ASN 수신 트리거에 의해 생성 로직이 실행되어야함.
         * TODO: 3. ASN 수신시 입고 예정일을 받아서 Inbound의 입고 예정일 상태를 변경해야함.
         * TODO: 4. 하나의 발주서로 여러 Inbound가 생성되지 않게 만드는 방어 로직 필요.
         */

        Member manager = memberRepository.findById(1L)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        Inbound inbound = Inbound.builder()
                .inboundCode(codeGenerator.generate(INBOUND_CODE_PREFIX))
                .purchaseOrder(purchaseOrder)
                .status(InboundStatus.SCHEDULED)
                .managerName(manager.getName())
                .description(purchaseOrder.getDescription())
                .scheduledDate(purchaseOrder.getExpectedDate())
                .build();

        purchaseOrder.getProducts().forEach(purchaseOrderProduct -> {
            InboundProduct.builder()
                    .inbound(inbound)
                    .product(purchaseOrderProduct.getProduct())
                    .purchaseOrderProduct(purchaseOrderProduct)
                    .receivedQuantity(0)
                    .description(purchaseOrderProduct.getDescription())
                    .build();
        });

        return inboundRepository.save(inbound).getId();
    }

    public InboundReadDto findById(Long id) {
        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));
        return InboundReadDto.from(inbound);
    }

    public Page<InboundReadDto> inboundList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inbound> paging = inboundRepository.findAllWithPaging(pageable);
        return paging.map(InboundReadDto::from);
    }

    @Transactional
    public void updateInboundStatus(Long inboundId, InboundStatusUpdateRequest requestDto) {
        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));
        inbound.updateStatus(requestDto.status());
    }

    @Transactional
    public void updateInspection(Long inboundId, List<InboundInspectionUpdateRequest> requestList) {
        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));

        if (inbound.getStatus() != InboundStatus.INSPECTING) {
            throw new InboundException(INBOUND_INVALID_STATUS_FOR_INSPECTION);
        }

        for (InboundInspectionUpdateRequest request : requestList) {
            InboundProduct product = inbound.findProductById(request.inboundProductId())
                    .orElseThrow(() -> new InboundException(InboundExceptionType.INBOUND_PRODUCT_NOT_FOUND));
            product.updateInspectionResult(request.receivedQuantity());
        }

        // 검수 완료 시 적치 작업으로 변경
        inbound.updateStatus(InboundStatus.PUTAWAY);
    }

    @Transactional
    public void cancel(Long id) {
        Inbound inbound = inboundRepository.findById(id)
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));

        if (inbound.getStatus() != InboundStatus.CREATED && inbound.getStatus() != InboundStatus.SCHEDULED) {
            throw new InboundException(InboundExceptionType.INBOUND_CANNOT_CANCEL);
        }
        inbound.cancelInbound();
    }

    @Transactional
    public void arriveDelivery(Long inboundId) {
        Inbound inbound = inboundRepository.findById(inboundId)
                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));
        inbound.updateStatus(InboundStatus.ARRIVED);

        PurchaseOrder purchaseOrder = inbound.getPurchaseOrder();
        purchaseOrder.completeDelivery();

        inboundTaskFactory.createInspectionTask(inboundId);
    }

    // TODO: 삭제? 발주서가 없는 입고는 어떻게 처리할까
    @Transactional
    public Long create(InboundCreateRequestDto requestDto) {
        requestDto.validate();

        // 1. Inbound Entity
        Inbound inbound = createBaseInbound(requestDto);

        // 2. 발주서를 통한 입고 상품 생성
        addItemsFromPurchaseOrder(requestDto, inbound);

        // 3. 상품을 통한 입고 상품 생성
        addDirectItems(requestDto, inbound);

        return inboundRepository.save(inbound).getId();
    }

    // TODO: 삭제?
//    @Transactional
//    public Long update(InboundStatusUpdateRequest requestDto, Long id) {
//        Member manager = memberRepository.findById(requestDto.getMemberId())
//                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
//
//        Inbound inbound = inboundRepository.findById(id)
//                .orElseThrow(() -> new InboundException(INBOUND_NOT_FOUND));
//
//        inbound.updateData(manager.getName(), requestDto.getScheduledDate(), requestDto.getDescription());
//
//        if (requestDto.getItems() != null && !requestDto.getItems().isEmpty()) {
//            List<InboundProduct> items = requestDto.getItems().stream()
//                    .map(itemDto -> convertToEntity(itemDto, inbound))
//                    .toList();
//            inbound.updateItems(items);
//        }
//
//        return inbound.getId();
//    }


    private Inbound createBaseInbound(InboundCreateRequestDto requestDto) {
        Member manager = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        return Inbound.builder()
                .inboundCode(codeGenerator.generate(INBOUND_CODE_PREFIX))
                .status(InboundStatus.SCHEDULED)
                .managerName(manager.getName())
                .scheduledDate(requestDto.getScheduledDate())
                .description(requestDto.getDescription())
                .build();
    }

    private void addItemsFromPurchaseOrder(InboundCreateRequestDto requestDto, Inbound inbound) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(requestDto.getPurchaseOrderId())
                .orElseThrow(() -> new PurchaseOrderException(PURCHASE_ORDER_NOT_FOUND));

        purchaseOrder.getProducts().forEach(poItem -> {
            InboundProduct inboundItem = InboundProduct.builder()
                    .product(poItem.getProduct())
                    .purchaseOrderProduct(poItem)
                    .receivedQuantity(0)
                    .description(poItem.getDescription())
                    .build();

            inboundItem.assignInbound(inbound);
        });
    }

    private void addDirectItems(InboundCreateRequestDto requestDto, Inbound inbound) {
        if (requestDto.getItems() == null || requestDto.getItems().isEmpty()) {
            return;
        }

        requestDto.getItems().forEach(itemDto -> {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

            InboundProduct inboundItem = InboundProduct.builder()
                    .product(product)
                    .inbound(inbound)
                    .purchaseOrderProduct(null)
                    .receivedQuantity(itemDto.getQuantity())
                    .description(itemDto.getDescription())
                    .build();
        });
    }

    private InboundProduct convertToEntity(InboundProductDto dto, Inbound inbound) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_FOUND));

        return InboundProduct.builder()
                .inbound(inbound)
                .product(product)
                .receivedQuantity(dto.getQuantity())
                .description(dto.getDescription())
                .build();
    }
}
