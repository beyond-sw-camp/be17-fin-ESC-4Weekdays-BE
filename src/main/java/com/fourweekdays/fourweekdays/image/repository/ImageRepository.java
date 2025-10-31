package com.fourweekdays.fourweekdays.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fourweekdays.fourweekdays.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
