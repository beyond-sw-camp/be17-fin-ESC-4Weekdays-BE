package com.fourweekdays.fourweekdays.announcement.model.dto.request;

import com.fourweekdays.fourweekdays.announcement.model.entity.Announcement;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnnouncementCreateDto {

    private String title;
    private String content;
    private Boolean pinned;
    private String name;
    public Announcement toEntity() {
        return Announcement.builder()
                .title(title)
                .name(name)
                .content(content)
                .pinned(pinned)
                .build();
    }
}
