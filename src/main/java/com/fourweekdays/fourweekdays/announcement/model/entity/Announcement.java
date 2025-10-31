package com.fourweekdays.fourweekdays.announcement.model.entity;

import com.fourweekdays.fourweekdays.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;



@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String content; // 내용

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true ; //활성 상태 (삭제 시 false)

    @Builder.Default
    @Column(nullable = false)
    private Boolean pinned = false; // 중요 공지 여부 (true면 상단 고정)

    public void update(String title, String content , Boolean pinned) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (pinned != null) this.pinned = pinned;
    }
    public void isActive() {
        this.active = false;
    }
}