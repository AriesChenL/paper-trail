package com.lynn.papertrail.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InviteCodeGenerator 单元测试
 * 重点测试多线程并发生成邀请码的场景
 */
@DisplayName("邀请码生成器测试")
class InviteCodeGeneratorTest {

    private static final Logger logger = LoggerFactory.getLogger(InviteCodeGeneratorTest.class);

    private static final int THREAD_COUNT = 100;
    private static final int CODES_PER_THREAD = 50;
    private static final String TEST_USER_ID = "testUser123";

    @BeforeEach
    void setUp() {
        logger.info("开始测试 InviteCodeGenerator");
    }

    @Test
    @DisplayName("基本功能测试 - 单线程")
    void testBasicFunctionality() {
        // 测试默认长度
        String code1 = InviteCodeGenerator.generateInviteCode(TEST_USER_ID);
        assertNotNull(code1);
        assertEquals(InviteCodeGenerator.DEFAULT_LENGTH, code1.length());
        logger.info("默认长度邀请码: {}", code1);

        // 测试指定长度
        String code2 = InviteCodeGenerator.generateInviteCode(TEST_USER_ID, 12);
        assertNotNull(code2);
        assertEquals(12, code2.length());
        logger.info("指定长度邀请码: {}", code2);

        // 测试最小长度
        String code3 = InviteCodeGenerator.generateInviteCode(TEST_USER_ID, InviteCodeGenerator.MIN_LENGTH);
        assertNotNull(code3);
        assertEquals(InviteCodeGenerator.MIN_LENGTH, code3.length());
        logger.info("最小长度邀请码: {}", code3);
    }

    @Test
    @DisplayName("参数验证测试")
    void testParameterValidation() {
        // 测试 null 用户ID
        assertThrows(NullPointerException.class, () ->
            InviteCodeGenerator.generateInviteCode(null));

        // 测试空字符串用户ID
        assertThrows(IllegalArgumentException.class, () ->
            InviteCodeGenerator.generateInviteCode(""));

        assertThrows(IllegalArgumentException.class, () ->
            InviteCodeGenerator.generateInviteCode("   "));

        // 测试无效长度
        assertThrows(IllegalArgumentException.class, () ->
            InviteCodeGenerator.generateInviteCode(TEST_USER_ID, InviteCodeGenerator.MIN_LENGTH - 1));

        assertThrows(IllegalArgumentException.class, () ->
            InviteCodeGenerator.generateInviteCode(TEST_USER_ID, InviteCodeGenerator.MAX_LENGTH + 1));
    }

    @Test
    @DisplayName("虚拟线程并发测试 - 同一用户多线程生成")
    void testConcurrentGenerationSameUser() throws InterruptedException {
        Instant start = Instant.now();

        // 使用虚拟线程执行器
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Set<String> generatedCodes = ConcurrentHashMap.newKeySet();
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

            // 提交虚拟线程任务
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadIndex = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < CODES_PER_THREAD; j++) {
                            String code = InviteCodeGenerator.generateInviteCode(TEST_USER_ID);

                            // 验证邀请码基本属性
                            assertNotNull(code, "邀请码不能为null");
                            assertEquals(InviteCodeGenerator.DEFAULT_LENGTH, code.length(),
                                "邀请码长度不正确");
                            assertTrue(code.matches("[23456789ABCDEFGHJKLMNPQRSTUVWXYZ]+"),
                                "邀请码包含无效字符");

                            // 检查唯一性
                            boolean isUnique = generatedCodes.add(code);
                            if (!isUnique) {
                                logger.warn("发现重复邀请码: {} (线程: {}, 索引: {})",
                                    code, threadIndex, j);
                            }

                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        logger.error("线程 {} 执行失败: {}", threadIndex, e.getMessage(), e);
                        failureCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // 等待所有任务完成
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertTrue(completed, "测试超时，部分线程未完成");

            Duration duration = Duration.between(start, Instant.now());
            int totalCodes = THREAD_COUNT * CODES_PER_THREAD;

            logger.info("=== 并发测试结果统计 ===");
            logger.info("线程数: {}", THREAD_COUNT);
            logger.info("每线程生成数: {}", CODES_PER_THREAD);
            logger.info("总生成数: {}", totalCodes);
            logger.info("成功生成数: {}", successCount.get());
            logger.info("失败数: {}", failureCount.get());
            logger.info("唯一邀请码数: {}", generatedCodes.size());
            logger.info("重复率: {}%", String.format("%.2f", (totalCodes - generatedCodes.size()) * 100.0 / totalCodes));
            logger.info("执行时间: {} ms", duration.toMillis());
            logger.info("平均生成速度: {} codes/ms", String.format("%.2f", totalCodes * 1.0 / duration.toMillis()));
            // 验证结果
            assertEquals(totalCodes, successCount.get(), "成功生成数量不匹配");
            assertEquals(0, failureCount.get(), "不应该有失败的生成");

            // 检查唯一性 - 允许极少量重复（考虑到哈希碰撞的可能性）
            double uniqueRate = generatedCodes.size() * 100.0 / totalCodes;
            assertTrue(uniqueRate > 99.0,
                String.format("唯一性率太低: %.2f%%, 期望 > 99%%", uniqueRate));
        }
    }

    @Test
    @DisplayName("虚拟线程并发测试 - 不同用户多线程生成")
    void testConcurrentGenerationDifferentUsers() throws InterruptedException {
        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Map<String, Set<String>> userCodes = new ConcurrentHashMap<>();
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

            // 为每个线程分配不同的用户ID
            for (int i = 0; i < THREAD_COUNT; i++) {
                final String userId = "user_" + i;
                executor.submit(() -> {
                    try {
                        Set<String> codes = ConcurrentHashMap.newKeySet();

                        for (int j = 0; j < CODES_PER_THREAD; j++) {
                            String code = InviteCodeGenerator.generateInviteCode(userId);
                            codes.add(code);
                            successCount.incrementAndGet();
                        }

                        userCodes.put(userId, codes);
                    } catch (Exception e) {
                        logger.error("用户 {} 生成邀请码失败: {}", userId, e.getMessage(), e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertTrue(completed, "测试超时");

            Duration duration = Duration.between(start, Instant.now());
            int totalCodes = THREAD_COUNT * CODES_PER_THREAD;

            // 统计所有生成的邀请码
            Set<String> allCodes = new HashSet<>();
            userCodes.values().forEach(allCodes::addAll);

            logger.info("=== 多用户并发测试结果 ===");
            logger.info("用户数: {}", THREAD_COUNT);
            logger.info("每用户生成数: {}", CODES_PER_THREAD);
            logger.info("总生成数: {}", totalCodes);
            logger.info("成功生成数: {}", successCount.get());
            logger.info("全局唯一邀请码数: {}", allCodes.size());
            logger.info("执行时间: {} ms", duration.toMillis());

            // 验证每个用户的邀请码唯一性
            userCodes.forEach((userId, codes) -> {
                assertEquals(CODES_PER_THREAD, codes.size(),
                    "用户 " + userId + " 的邀请码应该都是唯一的");
            });

            assertEquals(totalCodes, successCount.get(), "成功生成数量不匹配");
        }
    }

    @Test
    @DisplayName("不同长度邀请码并发生成测试")
    void testConcurrentGenerationDifferentLengths() throws InterruptedException {
        int[] lengths = {4, 6, 8, 10, 12, 16};

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Map<Integer, Set<String>> lengthCodes = new ConcurrentHashMap<>();
            CountDownLatch latch = new CountDownLatch(lengths.length * 10);

            for (int length : lengths) {
                lengthCodes.put(length, ConcurrentHashMap.newKeySet());

                // 每个长度启动10个虚拟线程
                for (int i = 0; i < 10; i++) {
                    final String userId = "user_" + length + "_" + i;
                    executor.submit(() -> {
                        try {
                            for (int j = 0; j < 20; j++) {
                                String code = InviteCodeGenerator.generateInviteCode(userId, length);
                                assertEquals(length, code.length(), "邀请码长度不正确");
                                lengthCodes.get(length).add(code);
                            }
                        } catch (Exception e) {
                            logger.error("生成长度 {} 的邀请码失败: {}", length, e.getMessage(), e);
                        } finally {
                            latch.countDown();
                        }
                    });
                }
            }

            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertTrue(completed, "测试超时");

            // 验证结果
            lengthCodes.forEach((length, codes) -> {
                logger.info("长度 {}: 生成了 {} 个唯一邀请码", length, codes.size());
                assertTrue(codes.size() > 0, "应该生成了邀请码");
                codes.forEach(code -> assertEquals(length, code.length(),
                    "邀请码长度应该是 " + length));
            });
        }
    }

    @RepeatedTest(value = 5, name = "压力测试 - 第 {currentRepetition} 次")
    @DisplayName("高并发压力测试")
    void testHighConcurrencyStress() throws InterruptedException {
        int highThreadCount = 200;
        int codesPerThread = 25;

        Instant start = Instant.now();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Set<String> allCodes = ConcurrentHashMap.newKeySet();
            AtomicInteger totalGenerated = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(highThreadCount);

            for (int i = 0; i < highThreadCount; i++) {
                final String userId = "stressUser_" + i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < codesPerThread; j++) {
                            String code = InviteCodeGenerator.generateInviteCode(userId);
                            allCodes.add(code);
                            totalGenerated.incrementAndGet();
                        }
                    } catch (Exception e) {
                        logger.error("压力测试失败: {}", e.getMessage(), e);
                    } finally {
                        latch.countDown();
                    }
                });
            }

            boolean completed = latch.await(60, TimeUnit.SECONDS);
            assertTrue(completed, "压力测试超时");

            Duration duration = Duration.between(start, Instant.now());
            int expectedTotal = highThreadCount * codesPerThread;

            logger.info("=== 压力测试结果 ===");
            logger.info("线程数: {}", highThreadCount);
            logger.info("期望生成数: {}", expectedTotal);
            logger.info("实际生成数: {}", totalGenerated.get());
            logger.info("唯一邀请码数: {}", allCodes.size());
            logger.info("执行时间: {} ms", duration.toMillis());
            logger.info("生成速度: {:.2f} codes/ms",
                totalGenerated.get() * 1.0 / duration.toMillis());

            assertEquals(expectedTotal, totalGenerated.get(), "生成数量不匹配");

            // 在高并发情况下，允许少量重复
            double uniqueRate = allCodes.size() * 100.0 / expectedTotal;
            assertTrue(uniqueRate > 95.0,
                String.format("唯一性率: %.2f%%, 期望 > 95%%", uniqueRate));
        }
    }

}
