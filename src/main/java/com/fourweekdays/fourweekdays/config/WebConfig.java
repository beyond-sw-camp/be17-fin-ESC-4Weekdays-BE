package com.fourweekdays.fourweekdays.config;

import com.fourweekdays.fourweekdays.asn.resolver.VendorArgumentResolver;
import com.fourweekdays.fourweekdays.order.resolver.FranchiseArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final VendorArgumentResolver vendorArgumentResolver;
    private final FranchiseArgumentResolver franchiseArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(vendorArgumentResolver);
        resolvers.add(franchiseArgumentResolver);
    }
}