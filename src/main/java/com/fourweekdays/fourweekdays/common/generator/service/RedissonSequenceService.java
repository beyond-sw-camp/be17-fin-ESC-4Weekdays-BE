package com.fourweekdays.fourweekdays.common.generator.service;

import com.fourweekdays.fourweekdays.common.generator.entity.Sequence;
import com.fourweekdays.fourweekdays.common.generator.repository.SequenceRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedissonSequenceService {

    private final EntityManager entityManager;
    private final RedissonClient redissonClient;
    private final SequenceRepository repository;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final int WAIT_SEC = 5;
    private static final int LEASE_SEC = 8;
    private static final int MAX_RETRY = 5;

    @Transactional
    public String generate(String prefix) throws InterruptedException {

        String lockKey = "lock:seq:" + prefix;
        RLock lock = redissonClient.getLock(lockKey);

        while (true) {
            boolean locked = false;

            try {
                locked = lock.tryLock(WAIT_SEC, LEASE_SEC, TimeUnit.SECONDS);

                if (!locked) {
                    // 락 획득 실패 → 다시 시도
                    continue;
                }

                String today = LocalDate.now().format(DATE_FMT);

                Sequence seq = repository.findByPrefix(prefix)
                        .orElseGet(() -> repository.save(new Sequence(prefix, 0, today)));

                seq.increase();

                repository.save(seq);
                entityManager.flush();

                return format(prefix, today, seq.getCurrentValue());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            } finally {
                lock.unlock();
            }
        }
    }


    private String format(String prefix, String date, int value) {
        return String.format("%s-%s-%04d", prefix, date, value);
    }

}
