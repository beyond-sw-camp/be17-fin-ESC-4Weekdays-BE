package com.fourweekdays.fourweekdays.redis.generator;

import com.fourweekdays.fourweekdays.common.generator.entity.Sequence;
import com.fourweekdays.fourweekdays.common.generator.repository.SequenceRepository;
import com.fourweekdays.fourweekdays.common.generator.service.RedissonSequenceService;
import com.fourweekdays.fourweekdays.config.RedissonTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import({RedissonTestConfig.class, RedissonSequenceService.class})
class RedissonSequenceConcurrencyTest {

    @Autowired
    private RedissonSequenceService sequenceService;

    @Autowired
    private SequenceRepository repository;

    @Test
    void sequenceConcurrencyTest() throws Exception {
        String prefix = "ORD";
        repository.deleteAll();

        int total = 500;

        ExecutorService pool = Executors.newFixedThreadPool(64);
        Set<String> successCodes = ConcurrentHashMap.newKeySet();
        Set<String> failCodes = ConcurrentHashMap.newKeySet();

        CountDownLatch ready = new CountDownLatch(total);
        CountDownLatch start = new CountDownLatch(1);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < total; i++) {
            ready.countDown();
            pool.submit(() -> {
                try {
                    start.await();
                    String code = sequenceService.generate(prefix);
                    successCodes.add(code);
                } catch (Exception e) {
                    failCodes.add(e.getMessage());
                }
            });
        }

        ready.await();
        start.countDown();

        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);

        long timeMs = System.currentTimeMillis() - startTime;

        printResult(total, successCodes.size(), failCodes.size(), timeMs);

        Sequence seq = repository.findByPrefix(prefix)
                .orElseThrow();

        assertThat(seq.getCurrentValue()).isEqualTo(successCodes.size());
    }

    private void printResult(int total, int success, int fail, long timeMs) {
        System.out.println("\n================= 🔥 시퀀스 동시성 테스트 결과 =================");
        System.out.printf("총 요청 수 : %d\n", total);
        System.out.printf("발급 성공 수 : %d (%.2f%%)\n", success, (success * 100.0 / total));
        System.out.printf("발급 실패 수 : %d (%.2f%%)\n", fail, (fail * 100.0 / total));
        System.out.println("\n중복 여부 : " + (success == total));
        System.out.println("총 소요 시간(ms) : " + timeMs);
        System.out.printf("평균 처리 속도(QPS) : %.2f req/sec\n", success / (timeMs / 1000.0));
        System.out.println("=============================================================\n");
    }
}
