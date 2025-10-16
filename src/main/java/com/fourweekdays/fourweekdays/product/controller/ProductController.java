package com.fourweekdays.fourweekdays.product.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.common.BaseResponseStatus;
import com.fourweekdays.fourweekdays.product.dto.request.ProductCreateDto;
import com.fourweekdays.fourweekdays.product.dto.request.ProductUpdateDto;
import com.fourweekdays.fourweekdays.product.dto.response.ProductReadDto;
import com.fourweekdays.fourweekdays.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @Operation(
            summary = "상품 정보 등록",
            description = "등록한 상품의 정보를 저장한다."
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> register(@RequestBody ProductCreateDto dto) {
        Long saveId = productService.createProduct(dto);
        return ResponseEntity.ok(BaseResponse.success(saveId));
    }

    // 상품 전체 조회
    @Operation(
            summary = "상품 조회",
            description = "등록된 상품 목록을 조회한다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductReadDto>>> getProductList() {
        List<ProductReadDto> productList = productService.getProductList();
        return ResponseEntity.ok(BaseResponse.success(productList));
    }

    // 상품 상세 조회
    @Operation(
            summary = "상품 상세 조회",
            description = "요청받은 productId에 해당하는 상품을 조회한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductReadDto>> getProductDetails(@PathVariable Long id) {
        ProductReadDto productDto = productService.getProductDetails(id);
        if (productDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.error(BaseResponseStatus.PRODUCT_NOT_FOUND));
        }
        return ResponseEntity.ok(BaseResponse.success(productDto));
    }

    @Operation(
            summary = "상품 상태 변경",
            description = "요청받은 상품의 저장 정보를 변경한다."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> updateProduct(@PathVariable Long id,
                                                            @RequestBody ProductUpdateDto requestDto) {
        return ResponseEntity.ok(BaseResponse.success(productService.update(id, requestDto)));
    }


//    // 상품 상태 변경
//    @PatchMapping("/{id}")
//    public ResponseEntity<BaseResponse<ProductStatusResponseDto>> updateStatus(@PathVariable Long id, 
//                                                                               @RequestBody ProductStatusUpdateDto dto) {
//        ProductStatusHistory history = productservice.updateProductStatus(id, dto);
//        ProductStatusResponseDto response = ProductStatusResponseDto.from(history);
//        return ResponseEntity.ok(BaseResponse.success(response));
//    }
}
