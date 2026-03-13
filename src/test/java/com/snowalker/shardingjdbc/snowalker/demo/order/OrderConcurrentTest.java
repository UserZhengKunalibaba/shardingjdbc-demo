package com.snowalker.shardingjdbc.snowalker.demo.order;

import com.snowalker.shardingjdbc.snowalker.demo.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class OrderConcurrentTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testCreateOrderConcurrent() throws Exception {

        int threadCount = 100;     // 并发线程数（建议先 50~200）
        int requestCount = 10000; // 总请求数

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(requestCount);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < requestCount; i++) {

            pool.execute(() -> {
                try {
                    // 等待统一开始
                    startLatch.await();

                    // ⭐⭐⭐⭐⭐
                    // 这里写你的调用
                    // ⭐⭐⭐⭐⭐
                    orderService.addOrderASync();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        System.out.println("准备就绪，开始压测...");
        startLatch.countDown(); // 同时起跑

        finishLatch.await();

        long endTime = System.currentTimeMillis();

        System.out.println("总耗时(ms): " + (endTime - startTime));
        System.out.println("TPS: " + requestCount * 1000L / (endTime - startTime));

        pool.shutdown();
    }
}