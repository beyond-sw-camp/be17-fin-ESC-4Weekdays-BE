package com.fourweekdays.fourweekdays.member.service;

import com.fourweekdays.fourweekdays.announcement.model.dto.response.AnnouncementReadDto;
import com.fourweekdays.fourweekdays.announcement.model.entity.Announcement;
import com.fourweekdays.fourweekdays.member.exception.MemberException;
import com.fourweekdays.fourweekdays.member.exception.MemberExceptionType;
import com.fourweekdays.fourweekdays.member.model.UserAuth;
import com.fourweekdays.fourweekdays.member.model.dto.MemberResponseDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberSignUpDto;
import com.fourweekdays.fourweekdays.member.model.dto.MemberUpdateDto;
import com.fourweekdays.fourweekdays.member.model.entity.Member;
import com.fourweekdays.fourweekdays.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원등록
    public void register(MemberSignUpDto dto) {
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Member member = dto.toEntity(encodedPassword);
        memberRepository.save(member);
    }

    // 회원 전체 조회
    public List<MemberResponseDto> getMemberList() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }

    // 직원 상세 조회
    public MemberResponseDto getMemberDetails(Long id) {
        return memberRepository.findById(id)
                .map(MemberResponseDto::from)
                .orElse(null);
    }

    //직원 정보 수정
    @Transactional
    public Long update(Long id, MemberUpdateDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        member.update(dto.getName(), dto.getPhoneNumber(), dto.getPassword(),
                dto.getRole(), dto.getStatus());

        return member.getId();
    }

    //회원 조회
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다. : " + email));

        return UserAuth.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }

    //페이징 처리 조회
    public Page<MemberResponseDto> readAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> result = memberRepository.findAllWithPaging(pageable);
        return result.map(MemberResponseDto::from);
    }

    public void checkEmailDuplicate(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new MemberException(MemberExceptionType.DUPLICATE_EMAIL);
                });
    }
}