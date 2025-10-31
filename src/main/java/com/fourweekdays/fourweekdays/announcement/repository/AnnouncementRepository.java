package com.fourweekdays.fourweekdays.announcement.repository;

import com.fourweekdays.fourweekdays.announcement.model.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long>, AnnouncementRepositoryCustom {
}
