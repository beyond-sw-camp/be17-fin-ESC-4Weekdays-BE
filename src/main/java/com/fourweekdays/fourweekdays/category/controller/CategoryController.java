package com.fourweekdays.fourweekdays.category.controller;

import com.fourweekdays.fourweekdays.category.dto.request.CategoryCreateDto;
import com.fourweekdays.fourweekdays.category.dto.response.CategoryReadDto;
import com.fourweekdays.fourweekdays.category.service.CategoryService;
import com.fourweekdays.fourweekdays.common.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")

public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 등록
    @PostMapping
    public ResponseEntity<BaseResponse<Long>> register(@RequestBody CategoryCreateDto dto) {
        Long saveId = categoryService.register(dto);
        return ResponseEntity.ok(BaseResponse.success(saveId));
    }

    // 카테고리 전체 조회
    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryReadDto>>> getCategoryList() {
        List<CategoryReadDto> categoryList = categoryService.getCategoryList();
        return ResponseEntity.ok(BaseResponse.success(categoryList));
    }

}
