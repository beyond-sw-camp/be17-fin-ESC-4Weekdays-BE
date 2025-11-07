package com.fourweekdays.fourweekdays.config;

import com.fourweekdays.fourweekdays.asn.filter.VendorApiKeyFilter;
import com.fourweekdays.fourweekdays.member.config.filter.JwtAuthFilter;
import com.fourweekdays.fourweekdays.member.config.filter.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration configuration;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http, VendorApiKeyFilter vendorApiKeyFilter) throws Exception {
        http.authorizeHttpRequests(
                (auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        //관리자만 가능(생성)
                        .requestMatchers(HttpMethod.POST,
                                "/api/announcement", "/api/franchises", "/api/products",
                                "/api/vendors", "/api/warehouses", "/api/category", "/api/member/signup",
                                "/api/purchase-orders"
                        ).hasRole("ADMIN")
                        //관리자만 가능(변경)
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/announcement/**", "/api/franchises/**", "/api/products/**",
                                "/api/vendors/**", "/api/warehouses/**", "/api/member/**",
                                "/api/inbounds/**",
                                "/api/outbounds/**",
                                "/api/purchase-orders/**"
                        ).hasRole("ADMIN")
                        //관리자만 가능(삭제)
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/announcement/**", "/api/franchises/**", "/api/products/**",
                                "/api/vendors/**", "/api/warehouses/**", "/api/inbounds/**",
                                "/api/purchase-orders/**"
                        ).hasRole("ADMIN")
                        //관리자만 가능(asn,order)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 3. 작업자(WORKER) 이상 접근 가능 경로
                        .requestMatchers("/api/tasks/**", "/api/inbound-tasks/**").hasAnyRole("ADMIN", "MANAGER", "WORKER")

                        // 4. 매니저(MANAGER) 이상 접근 가능 경로 (주로 GET 조회)
                        .requestMatchers(HttpMethod.GET,
                                "/api/announcement/**", "/api/franchises/**", "/api/products/**",
                                "/api/vendors/**", "/api/warehouses/**", "/api/category/**",
                                "/api/member/**", "/api/purchase-orders/**", "/api/inbounds/**",
                                "/api/outbounds/**", "/api/inventories/**", "/api/locations/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        // 5. 외부 API (Filter에서 인증 처리)
                        .requestMatchers("/api/vendor/asn/**", "/api/franchise/order/**").authenticated()

                        // 6. 그 외 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
        );

        // 이하 CORS, CSRF, Logout 등 나머지 설정은 동일
        http.cors(cors ->
                cors.configurationSource(corsConfigurationSource()));

        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("4weekdays")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{'''message''':'''로그아웃에 성공하였습니다.'''}");
                })
        );

        http.addFilterBefore(vendorApiKeyFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(new LoginFilter(configuration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
