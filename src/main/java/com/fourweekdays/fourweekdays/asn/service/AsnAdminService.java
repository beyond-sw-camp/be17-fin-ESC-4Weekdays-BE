package com.fourweekdays.fourweekdays.asn.service;

import com.fourweekdays.fourweekdays.asn.exception.AsnException;
import com.fourweekdays.fourweekdays.asn.model.dto.response.AsnResponse;
import com.fourweekdays.fourweekdays.asn.model.entity.Asn;
import com.fourweekdays.fourweekdays.asn.repository.AsnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.fourweekdays.fourweekdays.asn.exception.AsnExceptionType.ASN_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class AsnAdminService {

    private final AsnRepository asnRepository;

    public AsnResponse asnDetail(Long asnId) {
        Asn asn = asnRepository.findById(asnId)
                .orElseThrow(() -> new AsnException(ASN_NOT_FOUND));
        return AsnResponse.toDto(asn);
    }

    public Page<AsnResponse> asnListByPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Asn> pageList = asnRepository.findAllWithPaging(pageable);
        return pageList.map(AsnResponse::toDto);
    }

}
