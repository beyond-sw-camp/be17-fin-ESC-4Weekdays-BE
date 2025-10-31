package com.fourweekdays.fourweekdays.member.controller;

import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.member.model.dto.*;
import com.fourweekdays.fourweekdays.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<BaseResponse<String>> register(@Valid @RequestBody MemberSignUpDto dto) {
        memberService.register(dto);
        return ResponseEntity.ok(BaseResponse.success("등록 완료"));
    }

    //직원 페이징 처리 조회
    // 직원 목록 조회
    @Operation(
            summary = "직원 목록 조회",
            description = "시스템에 등록된 직원들의 목록을 불러온다."
    )
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<MemberResponseDto>>> memberReads(@RequestParam(defaultValue = "0") Integer page,
                                                                             @RequestParam(defaultValue = "10") Integer size) {
        Page<MemberResponseDto> result = memberService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
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

    //이메일 중복체크 기능
    @PostMapping("/check-email")
    public ResponseEntity<BaseResponse<String>> checkEmail(@RequestBody MemberEmailCheckDto dto) {
        memberService.checkEmailDuplicate(dto.getEmail());
        return ResponseEntity.ok(BaseResponse.success("사용 가능한 이메일입니다."));
    }

    //검색 기능
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<MemberResponseDto>>> searchMember(
            MemberSearchDto dto,
            Pageable pageable
    ) {
        Page<MemberResponseDto> result = memberService.searchMembers(dto, pageable);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
