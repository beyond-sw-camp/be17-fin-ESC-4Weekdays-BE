package com.fourweekdays.fourweekdays.order.contorller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.order.model.dto.response.OrderReadDto;
import com.fourweekdays.fourweekdays.order.service.OrderAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/order")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderAdminService orderAdminService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderReadDto>> orderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(orderAdminService.orderDetail(id)));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<OrderReadDto>>> orderList(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.success(orderAdminService.orderList(page, size)));
    }
}
