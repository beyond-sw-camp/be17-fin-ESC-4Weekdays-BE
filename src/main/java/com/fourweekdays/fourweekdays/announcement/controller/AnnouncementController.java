package com.fourweekdays.fourweekdays.announcement.controller;

import com.fourweekdays.fourweekdays.announcement.model.dto.request.AnnouncementCreateDto;
import com.fourweekdays.fourweekdays.announcement.model.dto.request.AnnouncementUpdateDto;
import com.fourweekdays.fourweekdays.announcement.model.dto.response.AnnouncementReadDto;
import com.fourweekdays.fourweekdays.announcement.service.AnnouncementService;
import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.member.model.UserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<BaseResponse<Long>> announcementCreate(@RequestBody AnnouncementCreateDto dto,
                                                                 @AuthenticationPrincipal UserAuth userAuth) {
        String name = userAuth.getName();
        Long result = announcementService.create(dto, name);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AnnouncementReadDto>> announcementRead(@PathVariable Long id) {
        AnnouncementReadDto result = announcementService.read(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity<BaseResponse<Page<AnnouncementReadDto>>> announcementReads(@RequestParam(defaultValue = "0") Integer page,
                                                                                     @RequestParam(defaultValue = "10") Integer size) {
        Page<AnnouncementReadDto> result = announcementService.readAll(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponse<Long>> announcementUpdate(@RequestBody AnnouncementUpdateDto dto, @PathVariable Long id) {
        Long result = announcementService.update(dto, id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> announcementDelete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(BaseResponse.success("공지 삭제"));
    }

}