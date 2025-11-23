package com.fourweekdays.fourweekdays.location.controller;


import com.fourweekdays.fourweekdays.common.BaseResponse;
import com.fourweekdays.fourweekdays.location.model.dto.response.LocationResponse;
import com.fourweekdays.fourweekdays.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/locations")
@RequiredArgsConstructor
@RestController
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/available")
    public ResponseEntity<BaseResponse<List<LocationResponse>>> getAvailableLocations() {
        return ResponseEntity.ok(BaseResponse.success(locationService.getAvailableLocationsByVendor()));
    }

    @GetMapping("/{locationCode}")
    public ResponseEntity<BaseResponse<LocationResponse>> getLocationByCode(@PathVariable String locationCode) {
        return ResponseEntity.ok(BaseResponse.success(locationService.getLocationByCode(locationCode)));
    }

    @GetMapping("/by-vendor/{vendorId}")
    public ResponseEntity<BaseResponse<List<LocationResponse>>> getLocationsByVendor(@PathVariable Long vendorId) {
        return ResponseEntity.ok(BaseResponse.success(locationService.getLocationsByVendor(vendorId)));
    }
}
