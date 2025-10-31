package com.fourweekdays.fourweekdays.member.model.dto;

import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.product.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignUpDto {

    @Schema(description = "이메일 입력", example = "test@test.com")
    @NotBlank(message = "이메일 입력을 해주세요")
    @Pattern(regexp = "^[a-zA-Z0-9+-._]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @Schema(description = "비밀번호 입력", example = "qwer1234")
    @NotBlank(message = "비밀번호을 입력해 주세요")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$", message = "비밀번호는 영문과 숫자를 포함하여 8자 이상이어야 합니다.")
    private String password;

    @Schema(description = "이름을 입력", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @Schema(description = "핸드폰 번호 입력", example = "01012341234")
    @NotBlank(message = "핸드폰번호를 입력해 주세요")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "핸드폰번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @Schema(description = "아이디 권한 선택", example = "WORKER")
    @NotNull(message = "권한을 선택해주세요")
    private MemberRole role;

    @NotNull(message = "상태를 선택해주세요")
    private AuthStatus status;

    //엔티티 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .phoneNumber(phoneNumber)
                .role(role)
                .status(status)
                .joinAt(LocalDateTime.now())
                .build();
    }

}
