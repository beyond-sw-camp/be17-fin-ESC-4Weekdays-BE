package com.fourweekdays.fourweekdays.asn.filter;

import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class VendorApiKeyFilter extends OncePerRequestFilter {

    private final VendorRepository vendorRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/vendor/asn")) {
            filterChain.doFilter(request, response);
            return;
        }



        String apiKey = request.getHeader("X-API-Key");

        log.info("api_key={}", apiKey);

        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"API Key가 필요합니다.\"}");
            return;
        }

        // Vendor 검증
        Vendor vendor = vendorRepository.findByApiKey(apiKey)
                .orElse(null);

        if (vendor == null || vendor.getStatus() != VendorStatus.ACTIVE) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"유효하지 않은 API Key입니다.\"}");
            return;
        }

        log.info("vendor={}", vendor.getName());
        // 인증된 Vendor 정보를 request attribute에 저장
        request.setAttribute("authenticatedVendor", vendor);

        filterChain.doFilter(request, response);
    }
}
