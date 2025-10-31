package com.fourweekdays.fourweekdays.member.config.utils;

import com.fourweekdays.fourweekdays.member.model.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "abcdeffghijklmnopqrstuvwxyz0123456";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final Long EXP = 1000 * 60 * 10L;

    public static final String IDX_NAME = "idx";
    public static final String EMAIL_NAME = "email";
    public static final String NAME_NAME = "name";
    public static final String ROLE_NAME = "role";
    public static final String TOKEN_NAME = "4weekdays";

    public static String generateToken(Long id, String email, String name, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(IDX_NAME, id);
        claims.put(EMAIL_NAME, email);
        claims.put(NAME_NAME, name);
        claims.put(ROLE_NAME, role);

        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    //JWT 토큰에서 특정 key 값 가져오기
    public static String getValue(Claims claims, String key) {
        Object value = claims.get(key);
        return value != null ? value.toString() : null;     // null-safe 처리(null 값 방어)
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}