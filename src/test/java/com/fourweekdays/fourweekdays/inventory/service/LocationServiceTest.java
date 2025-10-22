package com.fourweekdays.fourweekdays.inventory.service;

import com.fourweekdays.fourweekdays.FourWeekdaysApplication;
import com.fourweekdays.fourweekdays.common.vo.Address;
import com.fourweekdays.fourweekdays.inventory.model.entity.Location;
import com.fourweekdays.fourweekdays.inventory.repository.LocationRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest(classes = FourWeekdaysApplication.class)
class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Vendor에게 처음으로 Location을 할당한다")
    void getLocationForVendor_신규할당() {
        // given
        Location emptyLocation = Location.builder().zone(1).build();
        locationRepository.save(emptyLocation);

        Vendor vendor = vendorRepository.save(Vendor.builder()
                .vendorCode("V-123412983")
                .name("아모레퍼시픽")
                .phoneNumber("02-6040-5114")
                .email("contact@amorepacific.com")
                .description("설화수, 라네즈 등 다양한 브랜드 보유")
                .status(VendorStatus.ACTIVE)
                .address(Address.builder()
                        .zipcode("04386")
                        .street("서울특별시 용산구 한강대로 100")
                        .detail("아모레퍼시픽 본사")
                        .build())
                .build());

        entityManager.flush();
        entityManager.clear();

        // when
        Location assignedLocation = locationService.getLocationForVendor(vendor);

        // then
        assertThat(assignedLocation).isNotNull();
        assertThat(assignedLocation.getVendor()).isEqualTo(vendor);
        assertThat(assignedLocation.isActive()).isTrue();
        assertThat(assignedLocation.getZone()).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 Location이 할당된 Vendor는 기존 Location을 반환한다")
    void ensureLocationForVendor_기존반환() {
        // given
        Vendor vendor = vendorRepository.save(Vendor.builder()
                .vendorCode("V-123412983")
                .name("아모레퍼시픽")
                .phoneNumber("02-6040-5114")
                .email("contact@amorepacific.com")
                .description("설화수, 라네즈 등 다양한 브랜드 보유")
                .status(VendorStatus.ACTIVE)
                .address(Address.builder()
                        .zipcode("04386")
                        .street("서울특별시 용산구 한강대로 100")
                        .detail("아모레퍼시픽 본사")
                        .build())
                .build());

        Location existingLocation = Location.builder().zone(2).build();
        existingLocation.assignVendor(vendor);
        locationRepository.save(existingLocation);

        entityManager.flush();
        entityManager.clear();

        // when
        Location result = locationService.getLocationForVendor(vendor);

        // then
        assertThat(result.getId()).isEqualTo(existingLocation.getId());
        assertThat(result.getZone()).isEqualTo(2);
    }

    @Test
    @DisplayName("할당 가능한 Location이 없으면 예외 발생")
    void ensureLocationForVendor_가용Location없음() {
        // given
        Vendor vendor = vendorRepository.save(Vendor.builder()
                .vendorCode("V-123412983")
                .name("아모레퍼시픽")
                .phoneNumber("02-6040-5114")
                .email("contact@amorepacific.com")
                .description("설화수, 라네즈 등 다양한 브랜드 보유")
                .status(VendorStatus.ACTIVE)
                .address(Address.builder()
                        .zipcode("04386")
                        .street("서울특별시 용산구 한강대로 100")
                        .detail("아모레퍼시픽 본사")
                        .build())
                .build());

        // 빈 Location 없음
        // when & then
        assertThatThrownBy(() -> locationService.getLocationForVendor(vendor))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("창고 꽉차서 놓을데 없음");
    }
}