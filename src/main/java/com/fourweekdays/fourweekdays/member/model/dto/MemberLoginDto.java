package com.fourweekdays.fourweekdays.member.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginDto {

    @Schema(description = "로그인 이메일", example = "test@test.com")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @Schema(description = "로그인 비밀번호", example = "qwer1234")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

}
