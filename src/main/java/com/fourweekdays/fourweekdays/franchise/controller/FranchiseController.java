package com.fourweekdays.fourweekdays.franchise.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.franchise.model.dto.request.FranchiseCreateDto;
import com.fourweekdays.fourweekdays.franchise.model.dto.request.FranchiseUpdateDto;
import com.fourweekdays.fourweekdays.franchise.model.dto.response.FranchiseReadDto;
import com.fourweekdays.fourweekdays.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/franchises")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> franchiseCreate(@RequestBody FranchiseCreateDto dto) {
        Long result = franchiseService.create(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<FranchiseReadDto>> franchiseRead(@PathVariable Long id) {
        FranchiseReadDto result = franchiseService.read(id);
        return  ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<FranchiseReadDto>>> franchiseReads(@RequestParam(defaultValue = "0") Integer page,
                                                                         @RequestParam(defaultValue = "10") Integer size)  {
        Page<FranchiseReadDto> result = franchiseService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> franchiseUpdate(@RequestBody FranchiseUpdateDto dto, @PathVariable Long id) {
        Long result = franchiseService.update(dto, id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> franchiseDelete(@PathVariable Long id) {
        franchiseService.suspend(id);
        return ResponseEntity.ok(BaseResponse.success("거래 중단"));
    }
}
