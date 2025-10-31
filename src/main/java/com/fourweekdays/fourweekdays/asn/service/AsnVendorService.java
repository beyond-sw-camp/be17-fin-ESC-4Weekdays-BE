package com.fourweekdays.fourweekdays.asn.service;

import com.fourweekdays.fourweekdays.asn.exception.AsnException;
import com.fourweekdays.fourweekdays.asn.model.dto.request.AsnReceiveRequest;
import com.fourweekdays.fourweekdays.asn.model.dto.request.PurchaseOrderRejectRequest;
import com.fourweekdays.fourweekdays.asn.model.dto.response.AsnReceiveResponse;
import com.fourweekdays.fourweekdays.asn.model.entity.Asn;
import com.fourweekdays.fourweekdays.asn.model.entity.AsnStatus;
import com.fourweekdays.fourweekdays.asn.repository.AsnRepository;
import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.inbound.service.InboundService;
import com.fourweekdays.fourweekdays.purchaseorder.exception.PurchaseOrderException;
import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import com.fourweekdays.fourweekdays.purchaseorder.repository.PurchaseOrderRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.asn.exception.AsnExceptionType.ASN_ALREADY_EXISTS;
import static com.fourweekdays.fourweekdays.asn.exception.AsnExceptionType.VENDOR_MISMATCH;
import static com.fourweekdays.fourweekdays.purchaseorder.exception.PurchaseOrderExceptionType.*;
import static com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrderStatus.APPROVED;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AsnVendorService {

    private static final String ASN_CODE_PREFIX = "ASN";

    private final AsnRepository asnRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InboundService inboundService;
    private final CodeGenerator codeGenerator;

    @Transactional
    public AsnReceiveResponse receiveAsn(Vendor vendor, AsnReceiveRequest request) {
        PurchaseOrder purchaseOrder = findAndValidatePurchaseOrder(vendor, request.orderCode());

        if (purchaseOrder.getStatus() != APPROVED) {
            throw new PurchaseOrderException(PURCHASE_ORDER_INVALID_STATUS_FOR_ASN);
        }

        if (asnRepository.existsByPurchaseOrder(purchaseOrder)) {
            throw new AsnException(ASN_ALREADY_EXISTS);
        }

        Asn asn = Asn.create(
                vendor,
                purchaseOrder,
                codeGenerator.generate(ASN_CODE_PREFIX),
                request.expectedDate(),
                request.description(),
                AsnStatus.ACCEPTED
        );
        asnRepository.save(asn);

        // inbound 자동 생성
        purchaseOrder.awaitDelivery(); // 배송대기 상태로 변경
        inboundService.createByPurchaseOrder(purchaseOrder); // 발주서에 따른 입고서 생성

        return AsnReceiveResponse.builder()
                .asnCode(asn.getAsnCode())
                .message("ASN이 성공적으로 등록되었습니다")
                .build();
    }

    @Transactional
    public void rejectPurchaseOrderByVendor(Vendor vendor, PurchaseOrderRejectRequest request) {
        PurchaseOrder purchaseOrder = findAndValidatePurchaseOrder(vendor, request.orderCode());

        if (purchaseOrder.getStatus() != APPROVED) {
            throw new PurchaseOrderException(PURCHASE_ORDER_CANNOT_REJECT);
        }

        purchaseOrder.rejectByVendor(request.description());

        // reject된 발주건 테이블 저장
        Asn rejectedAsn = Asn.create(
                vendor,
                purchaseOrder,
                codeGenerator.generate(ASN_CODE_PREFIX),
                null,
                request.description(),
                AsnStatus.REJECTED
        );
        asnRepository.save(rejectedAsn);
    }


    private PurchaseOrder findAndValidatePurchaseOrder(Vendor vendor, String orderCode) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new PurchaseOrderException(PURCHASE_ORDER_NOT_FOUND));

        Long purchaseOrderVendorId = purchaseOrder.getVendor().getId();
        if (!purchaseOrderVendorId.equals(vendor.getId())) {
            throw new AsnException(VENDOR_MISMATCH);
        }

        return purchaseOrder;
    }
}
