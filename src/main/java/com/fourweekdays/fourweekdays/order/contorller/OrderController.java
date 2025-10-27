package com.fourweekdays.fourweekdays.order.contorller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.order.model.dto.response.OrderReadDto;
import com.fourweekdays.fourweekdays.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    // 내부에서 사용하는 CRUD R만 만들거임
    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<OrderReadDto>> OrderDetail(@PathVariable Long id) {
        OrderReadDto result = orderService.readd(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
