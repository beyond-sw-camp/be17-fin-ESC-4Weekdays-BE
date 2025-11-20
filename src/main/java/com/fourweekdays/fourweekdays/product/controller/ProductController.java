package com.fourweekdays.fourweekdays.product.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductCreateDto;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductSearchRequest;
import com.fourweekdays.fourweekdays.product.model.dto.request.ProductUpdateDto;
import com.fourweekdays.fourweekdays.product.model.dto.response.ProductReadDto;
import com.fourweekdays.fourweekdays.product.service.ProductEsService;
import com.fourweekdays.fourweekdays.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductEsService productEsService;

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> register(@Valid @RequestBody ProductCreateDto dto) {
        Long saveId = productService.createProduct(dto);
        return ResponseEntity.ok(BaseResponse.success(saveId));
    }

    @PostMapping("/search")
    public ResponseEntity<BaseResponse<Page<ProductReadDto>>> searchProducts(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @RequestBody ProductSearchRequest request) {
        return ResponseEntity.ok(BaseResponse.success(productService.searchProduct(request, page, size)));
    }

    @PostMapping("/search/es")
    public ResponseEntity<BaseResponse<Page<ProductReadDto>>> searchWithElasticsearch(@RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size,
                                                                                      @RequestBody ProductSearchRequest request) {
        log.info("[ES] page={}, size={}, request={}", page, size, request);
        return ResponseEntity.ok(BaseResponse.success(productEsService.searchProducts(request, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductReadDto>> getProductDetails(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(productService.getProductDetail(id)));
    }
    
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
}
