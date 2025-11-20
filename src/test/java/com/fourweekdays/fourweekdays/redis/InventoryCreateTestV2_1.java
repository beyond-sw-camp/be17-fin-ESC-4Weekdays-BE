package com.fourweekdays.fourweekdays.redis;

import com.fourweekdays.fourweekdays.common.generator.InventoryIncreaseV2Service;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InventoryCreateTestV2_1 {

    @Test
    void synchronized_서버2대면_깨짐() throws Exception {

        InventoryIncreaseV2Service server1 = new InventoryIncreaseV2Service();
        InventoryIncreaseV2Service server2 = new InventoryIncreaseV2Service();

        int threadCount = 200;
        int qty = 10;

        ExecutorService pool = Executors.newFixedThreadPool(64);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            boolean useServer1 = (i % 2 == 0);

            pool.submit(() -> {
                if (useServer1) {
                    server1.increase("PRD-A", qty);
                } else {
                    server2.increase("PRD-A", qty);
                }
                latch.countDown();
            });
        }

        latch.await();
        pool.shutdown();

        int total = server1.get("PRD-A") + server2.get("PRD-A");

        System.out.println("\n===== V2-1 서버2대 synchronized 테스트 =====");
        System.out.println("서버1: " + server1.get("PRD-A"));
        System.out.println("서버2: " + server2.get("PRD-A"));
        System.out.println("합계: " + total);

        assertThat(total).isLessThan(threadCount * qty); // 실패해야 정상
    }
}
