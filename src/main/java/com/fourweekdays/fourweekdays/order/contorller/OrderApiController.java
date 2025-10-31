package com.fourweekdays.fourweekdays.order.contorller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import com.fourweekdays.fourweekdays.order.anootation.AuthenticatedFranchise;
import com.fourweekdays.fourweekdays.order.model.dto.request.OrderReceiveOrderDto;
import com.fourweekdays.fourweekdays.order.model.dto.request.OrderRejectDto;
import com.fourweekdays.fourweekdays.order.service.OrderFranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/franchise/order")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderFranchiseService orderFranchiseService;

    @PostMapping
    public ResponseEntity<BaseResponse<String>> receiveCreateOrder(@AuthenticatedFranchise FranchiseStore franchiseStore,
                                                                   @RequestBody OrderReceiveOrderDto dto) {
        orderFranchiseService.receiveCreateOrder(franchiseStore, dto);
        return ResponseEntity.ok(BaseResponse.success("출고 주문 전송 성공"));
    }

    // 주문 취소
    @PostMapping("/reject")
    public ResponseEntity<BaseResponse<String>> rejectOrder(@AuthenticatedFranchise FranchiseStore franchiseStore,
                                                            @RequestBody OrderRejectDto dto) {
        orderFranchiseService.rejectOrder(franchiseStore, dto);
        return ResponseEntity.ok(BaseResponse.success("주문 취소"));
    }

}
