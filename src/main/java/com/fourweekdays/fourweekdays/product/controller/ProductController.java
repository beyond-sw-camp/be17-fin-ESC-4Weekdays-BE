package com.fourweekdays.fourweekdays.product.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductCreateDto;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductUpdateDto;
import com.fourweekdays.fourweekdays.product.model.dto.response.ProductReadDto;
import com.fourweekdays.fourweekdays.product.service.ProductService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @Operation(
            summary = "상품 정보 등록",
            description = "등록한 상품의 정보를 저장한다."
    )
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> register(@Valid @RequestBody ProductCreateDto dto) {
        Long saveId = productService.createProduct(dto);
        return ResponseEntity.ok(BaseResponse.success(saveId));
    }

    // 상품 전체 조회
    @Operation(
            summary = "상품 조회",
            description = "등록된 상품 목록을 조회한다."
    )
    @GetMapping
    public ResponseEntity<BaseResponse<Page<ProductReadDto>>> getProductList(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size) {
        Page<ProductReadDto> productList = productService.getProductList(page, size);
        return ResponseEntity.ok(BaseResponse.success(productList));
    }

    // 상품 상세 조회
    @Operation(
            summary = "상품 상세 조회",
            description = "요청받은 productId에 해당하는 상품을 조회한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductReadDto>> getProductDetails(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(productService.getProductDetail(id)));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(BaseResponse.success("품절 상태로 변경"));
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
