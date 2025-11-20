package com.fourweekdays.fourweekdays.redis;

import com.fourweekdays.fourweekdays.common.generator.InventoryIncreaseV2Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = InventoryIncreaseV2Service.class)
class InventoryCreateTestV2 {

    @Autowired private InventoryIncreaseV2Service service;

    @Test
    void 단일서버_synchronized_OK() throws Exception {

        int threadCount = 200;
        int qty = 10;

        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                service.increase("PRD-A", qty);
                latch.countDown();
            });
        }

        latch.await();
        pool.shutdown();

        int result = service.get("PRD-A");

        System.out.println("\n===== V2 synchronized 테스트 =====");
        System.out.println("최종 재고: " + result);

        assertThat(result).isEqualTo(threadCount * qty);
    }
}
