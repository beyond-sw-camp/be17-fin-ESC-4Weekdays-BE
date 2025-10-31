package com.fourweekdays.fourweekdays.announcement.repository;

import com.fourweekdays.fourweekdays.announcement.model.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementRepositoryCustom {
    Page<Announcement> findAllWithPaging(Pageable pageable);
}

