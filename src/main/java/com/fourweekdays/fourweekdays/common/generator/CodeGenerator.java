package com.fourweekdays.fourweekdays.common.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class CodeGenerator {

    /**
     * TODO: Redis 분산 락을 사용한 순차 번호 생성으로 변경 필요
     * 현재는 임시로 UUID 기반 난수 사용 (동시성 이슈 없으나 가독성 낮음)
     *  1. Redis 분산 락 + 날짜 시퀀스
     *  2. DB 시퀀스 테이블 + 비관적 락
     *  3. MariaDB SEQUENCE 객체 활용
     */
    public String generate(String prefix) {
        String datePrefix = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String randomSuffix = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return String.format("%s-%s-%s", prefix, datePrefix, randomSuffix); // 예: IB-20251016-A7F3B2
    }
}
