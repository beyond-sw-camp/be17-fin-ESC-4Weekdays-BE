package com.fourweekdays.fourweekdays.announcement.service;

import com.fourweekdays.fourweekdays.announcement.exception.AnnouncementException;
import com.fourweekdays.fourweekdays.announcement.model.dto.request.AnnouncementCreateDto;
import com.fourweekdays.fourweekdays.announcement.model.dto.request.AnnouncementUpdateDto;
import com.fourweekdays.fourweekdays.announcement.model.dto.response.AnnouncementReadDto;
import com.fourweekdays.fourweekdays.announcement.model.entity.Announcement;
import com.fourweekdays.fourweekdays.announcement.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.announcement.exception.AnnouncementExceptionType.ANNOUNCEMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public Long create(AnnouncementCreateDto dto ,String name) {
        Announcement result = announcementRepository.save(dto.toEntity(name));
        return result.getId();
    }

    public AnnouncementReadDto read(Long id) {
        Announcement result = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementException(ANNOUNCEMENT_NOT_FOUND));
        return AnnouncementReadDto.from(result);
    }

    public Page<AnnouncementReadDto> readAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Announcement> result = announcementRepository.findAllWithPaging(pageable);
        return result.map(AnnouncementReadDto::from);
    }

    @Transactional
    public Long update(AnnouncementUpdateDto dto, Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementException(ANNOUNCEMENT_NOT_FOUND));

        announcement.update(dto.getTitle(), dto.getContent(),dto.getPinned());
        return announcement.getId();

    }

    @Transactional
    public void delete(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new AnnouncementException(ANNOUNCEMENT_NOT_FOUND));
        announcement.isActive();
    }
}

