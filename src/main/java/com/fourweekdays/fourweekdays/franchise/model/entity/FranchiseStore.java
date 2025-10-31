package com.fourweekdays.fourweekdays.franchise.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.franchise.exception.FranchiseException;
import jakarta.persistence.*;
import lombok.*;

import static com.fourweekdays.fourweekdays.franchise.exception.FranchiseExceptionType.FRANCHISE_INVALID_STATUS;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FranchiseStore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 50, nullable = false)
    private String franchiseCode; // 공급업체 코드 (V-001, V-002 등)

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String email;

    @Column(unique = true)
    private String apiKey;

    private String description; // 업체 설명 및 비고

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FranchiseStatus status; // (ACTIVE / INACTIVE)

    // ===== 비즈니스 로직 ===== //
    public void update(String name, String phoneNumber, String email, String description, Address address) {
        if (name != null) this.name = name;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (email != null) this.email = email;
        if (description != null) this.description = description;
        if (address != null) this.address = address;
    }

    public void suspended() {
        this.status = FranchiseStatus.SUSPENDED;
    }

    public boolean canOrder() {
        return this.status == FranchiseStatus.ACTIVE;
    }

    // 원래 이렇게 안하고 그냥 FranchiseStatus.valueOf로 사용하는데 도메인 책임원칙대로 하면
    // 이렇게 해야하지 않을까 라는 생각으로 했습니다.
    public void changeStatus(FranchiseStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }

        switch (newStatus) {
            case ACTIVE -> activate();
            case INACTIVE -> inactive();
            case SUSPENDED -> suspended();
            default -> throw new FranchiseException(FRANCHISE_INVALID_STATUS);
        }
    }

    private void activate() {
        this.status = FranchiseStatus.ACTIVE;
    }

    private void inactive() {
        this.status = FranchiseStatus.INACTIVE;
    }

}