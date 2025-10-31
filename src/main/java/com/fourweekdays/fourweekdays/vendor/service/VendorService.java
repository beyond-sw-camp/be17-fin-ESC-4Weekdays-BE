package com.fourweekdays.fourweekdays.vendor.service;

import com.fourweekdays.fourweekdays.common.generator.CodeGenerator;
import com.fourweekdays.fourweekdays.vendor.exception.VendorException;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorCreateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.request.VendorUpdateDto;
import com.fourweekdays.fourweekdays.vendor.model.dto.response.VendorReadDto;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.vendor.exception.VendorExceptionType.VENDOR_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VendorService {

    public static final String VENDOR_CODE_PREFIX = "V";

    private final VendorRepository vendorRepository;
    private final CodeGenerator codeGenerator;

    @Transactional
    public Long create(VendorCreateDto dto) {
        Vendor result = vendorRepository.save(dto.toEntity(codeGenerator.generate(VENDOR_CODE_PREFIX)));

        return result.getId();
    }

    public VendorReadDto read(Long id) {
        Vendor entity = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorException(VENDOR_NOT_FOUND));
        return VendorReadDto.from(entity);
    }

    public Page<VendorReadDto> readAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vendor> vendors = vendorRepository.findAllWithPaging(pageable);
        return vendors.map(VendorReadDto::from);
    }

    // 내용 수정
    @Transactional
    public void update(Long id, VendorUpdateDto dto) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorException(VENDOR_NOT_FOUND));

        vendor.update(dto.getName(), dto.getPhoneNumber(), dto.getEmail(),
                dto.getDescription(), dto.getAddress());
    }

    // 상태 변경
    @Transactional
    public void updateStatus(Long id, VendorStatus status) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorException(VENDOR_NOT_FOUND));

        vendor.changeStatus(status);
    }

    // 거래 중단
    @Transactional
    public void suspend(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new VendorException(VENDOR_NOT_FOUND));
        vendor.changeStatus(VendorStatus.SUSPENDED);
    }

}
