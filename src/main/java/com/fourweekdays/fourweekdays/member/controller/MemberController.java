package com.fourweekdays.fourweekdays.member.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.member.model.dto.MemberResponseDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberSignUpDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberUpdateDto;
import com.fourweekdays.fourweekdays.member.service.MemberService;
import com.fourweekdays.fourweekdays.product.dto.request.ProductUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "직원 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    // 회원 등록
    @Operation(
            summary = "신규 직원 등록",
            description = "관리자 권한으로 신규 직원의 정보를 시스템에 등록한다."
    )
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody MemberSignUpDto dto) {
        memberService.register(dto);
        return ResponseEntity.ok(BaseResponse.success("등록 완료"));
    }

    // 직원 목록 조회
    @Operation(
            summary = "직원 목록 조회",
            description = "시스템에 등록된 직원들의 목록을 불러온다."
    )
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<List<MemberResponseDto>>> getMemberList() {
        List<MemberResponseDto> productList = memberService.getMemberList();
        return ResponseEntity.ok(BaseResponse.success(productList));
    }

    //직원 상세 조회
    @Operation(
            summary = "직원의 상세 정보를 조회",
            description = "직원의 고유 ID를 사용하여 해당 직원의 상세 정보를 조회한다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<MemberResponseDto>> getByMemberId(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(memberService.getMemberDetails(id)));
    }

    //직원 정보 수정
    @Operation(
            summary = "직원의 정보를 수정",
            description = "관리자 권한으로 기존 직원의 정보를 수정해 적용한다."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> updateMember(@PathVariable Long id,
                                                            @RequestBody MemberUpdateDto requestDto) {
        return ResponseEntity.ok(BaseResponse.success(memberService.update(id, requestDto)));
    }
}
