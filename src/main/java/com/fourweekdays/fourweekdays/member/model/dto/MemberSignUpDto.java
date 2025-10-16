package com.fourweekdays.fourweekdays.member.model.dto;

import com.fourweekdays.fourweekdays.member.model.entity.AuthStatus;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import com.fourweekdays.fourweekdays.product.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private String email;

    @Schema(description = "비밀번호 입력", example = "qwer1234")
    @NotBlank(message = "비밀번호을 입력해 주세요")
    private String password;

    @Schema(description = "이름을 입력", example = "홍길동")
    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @Schema(description = "핸드폰 번호 입력", example = "01012341234")
    @NotBlank(message = "핸드폰번호를 입력해 주세요")
    private String phoneNumber;

    @Schema(description = "아이디 권한 선택", example = "WORKER")
    @NotNull(message = "권한을 선택해 주세요")
    private MemberRole role;

    //엔티티 변환
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .name(name)
                .phoneNumber(phoneNumber)
                .role(role)
                .status(AuthStatus.ACTIVE)
                .joinAt(LocalDateTime.now())
                .build();
    }

}
