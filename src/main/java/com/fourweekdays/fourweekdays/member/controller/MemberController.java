package com.fourweekdays.fourweekdays.member.controller;

import com.fourweekdays.fourweekdays.announcement.model.dto.response.AnnouncementReadDto;
import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.member.model.dto.MemberEmailCheckDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberResponseDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberSignUpDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberUpdateDto;
import com.fourweekdays.fourweekdays.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    // 회원 등록
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<String>> register(@RequestBody MemberSignUpDto dto) {
        memberService.register(dto);
        return ResponseEntity.ok(BaseResponse.success("등록 완료"));
    }

    //직원 페이징 처리 조회
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<MemberResponseDto>>> memberReads(@RequestParam(defaultValue = "0") Integer page,
                                                                             @RequestParam(defaultValue = "10") Integer size) {
        Page<MemberResponseDto> result = memberService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    //직원 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<MemberResponseDto>> getByMemberId(@PathVariable Long id) {
        return ResponseEntity.ok(BaseResponse.success(memberService.getMemberDetails(id)));
    }

    //직원 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> updateMember(@PathVariable Long id,
                                                           @RequestBody MemberUpdateDto requestDto) {
        return ResponseEntity.ok(BaseResponse.success(memberService.update(id, requestDto)));
    }

    //이메일 중복체크 기능
    @PostMapping("/check-email")
    public  ResponseEntity<BaseResponse<String>> checkEmail(@RequestBody MemberEmailCheckDto dto) {
        memberService.checkEmailDuplicate(dto.getEmail());
        return ResponseEntity.ok(BaseResponse.success("사용 가능한 이메일입니다."));
    }
}
