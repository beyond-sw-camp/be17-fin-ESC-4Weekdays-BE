package com.fourweekdays.fourweekdays.announcement.model.dto.request;

import lombok.Getter;

@Getter
public class AnnouncementUpdateDto {
    private String title;
    private String content;
    private Boolean pinned;
}

