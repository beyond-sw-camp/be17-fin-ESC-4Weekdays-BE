package com.fourweekdays.fourweekdays.warehouse.service;

import com.fourweekdays.fourweekdays.warehouse.exception.WarehouseException;
import com.fourweekdays.fourweekdays.warehouse.model.dto.request.WarehouseCreateDto;
import com.fourweekdays.fourweekdays.warehouse.model.dto.request.WarehouseUpdateDto;
import com.fourweekdays.fourweekdays.warehouse.model.dto.response.WarehouseReadDto;
import com.fourweekdays.fourweekdays.warehouse.model.entity.Warehouse;
import com.fourweekdays.fourweekdays.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fourweekdays.fourweekdays.warehouse.exception.WarehouseExceptionType.WAREHOUSE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public Long create(WarehouseCreateDto dto) {
        Warehouse result = warehouseRepository.save(dto.toEntity());
        return result.getId();
    }

    public WarehouseReadDto read(Long id) {
        Warehouse result = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseException(WAREHOUSE_NOT_FOUND));
        return WarehouseReadDto.from(result);
    }

    public Page<WarehouseReadDto> readAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Warehouse> result = warehouseRepository.findAllWithPaging(pageable);
        return result.map(WarehouseReadDto::from);
    }

    @Transactional
    public Long update(WarehouseUpdateDto dto, Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseException(WAREHOUSE_NOT_FOUND));

        warehouse.update(dto.getName(), dto.getPhoneNumber(), dto.getEmail(), dto.getAddress());
        return warehouse.getId();

    }

    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseException(WAREHOUSE_NOT_FOUND));
        warehouse.isActive();
    }
}
