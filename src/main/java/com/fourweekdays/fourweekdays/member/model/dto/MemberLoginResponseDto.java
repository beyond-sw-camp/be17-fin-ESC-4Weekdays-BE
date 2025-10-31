package com.fourweekdays.fourweekdays.member.model.dto;

import com.fourweekdays.fourweekdays.member.model.UserAuth;
import com.fourweekdays.fourweekdays.member.model.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginResponseDto {

    private Long id;
    private String email;
    private String name;
    private MemberRole role;

    public static MemberLoginResponseDto from(UserAuth userAuth) {
        return MemberLoginResponseDto.builder()
                .id(userAuth.getId())
                .email(userAuth.getEmail())
                .name(userAuth.getName())
                .role(userAuth.getRole())
                .build();
    }
}
