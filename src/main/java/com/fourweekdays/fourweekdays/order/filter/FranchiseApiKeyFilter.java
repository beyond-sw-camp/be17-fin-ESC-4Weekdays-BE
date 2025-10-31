package com.fourweekdays.fourweekdays.order.filter;

import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStatus;
import com.fourweekdays.fourweekdays.franchise.model.entity.FranchiseStore;
import com.fourweekdays.fourweekdays.franchise.repository.FranchiseRepository;
import com.fourweekdays.fourweekdays.vendor.model.entity.Vendor;
import com.fourweekdays.fourweekdays.vendor.model.entity.VendorStatus;
import com.fourweekdays.fourweekdays.vendor.repository.VendorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class FranchiseApiKeyFilter extends OncePerRequestFilter {

    private final FranchiseRepository franchiseRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/api/franchise/order")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-API-Key");

        if (apiKey == null || apiKey.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"API Key가 필요합니다.\"}");
            return;
        }

        FranchiseStore franchiseStore = franchiseRepository.findByApiKey(apiKey)
                .orElse(null);

        if (franchiseStore == null || franchiseStore.getStatus() != FranchiseStatus.ACTIVE) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"유효하지 않은 API Key입니다.\"}");
            return;
        }

        request.setAttribute("authenticatedFranchise", franchiseStore);

        filterChain.doFilter(request, response);
    }
}
