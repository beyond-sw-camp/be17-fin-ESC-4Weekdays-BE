package com.fourweekdays.fourweekdays.member.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 직원/사용자 ID (PK)

    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private LocalDateTime joinAt;  // 입사일 or 창고 전입일

    @Enumerated(EnumType.STRING)
    private MemberRole role; // 직급 / 역할

    @Enumerated(EnumType.STRING)
    private AuthStatus status; // 계정 상태 (ACTIVE=활성, INACTIVE=비활성, LOCK=잠금)

    // ===== 로직 ===== //
    public void update(String name, String phoneNumber, String password,
                       MemberRole role, AuthStatus status) {
        if (name != null)this.name = name;
        if (password != null)this.password = password;
        if (phoneNumber != null)this.phoneNumber = phoneNumber;
        if (role != null)this.role = role;
        if (status != null)this.status = status;
    }
}
