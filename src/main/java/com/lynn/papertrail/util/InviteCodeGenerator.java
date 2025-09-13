package com.lynn.papertrail.util;

import java.io.Serial;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * 邀请码生成工具类
 * <p>
 * 功能特性：
 * - 基于用户ID生成指定长度的邀请码
 * - 使用SHA-256算法确保安全性
 * - 去除易混淆字符（0,O,I,1等）
 * - 结合纳秒时间戳和随机数避免重复
 * - 支持自定义填充字符
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 使用默认配置生成8位邀请码
 * String code = InviteCodeGenerator.generateInviteCode("user123", 8);
 *
 * // 使用自定义填充字符
 * String code = InviteCodeGenerator.generateInviteCode("user123", 8, 'X');
 * }</pre>
 *
 * @author lynn
 * @version 1.0
 * @since 1.0
 */
public final class InviteCodeGenerator {

    /**
     * 最小邀请码长度
     */
    public static final int MIN_LENGTH = 4;

    /**
     * 最大邀请码长度
     */
    public static final int MAX_LENGTH = 32;

    /**
     * 默认邀请码长度
     */
    public static final int DEFAULT_LENGTH = 8;

    /**
     * 默认的填充字符
     */
    private static final char DEFAULT_PADDING_CHAR = '2';

    /**
     * 去除易混淆字符的大写字符集 (去除0,O,I,1等易混淆字符)
     * 包含32个字符，便于进制转换
     */
    private static final String CLEAR_UPPERCASE_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    /**
     * 字符集基数
     */
    private static final int BASE = CLEAR_UPPERCASE_CHARS.length();

    /**
     * 线程安全的随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 私有构造函数，防止实例化
     */
    private InviteCodeGenerator() {
        throw new UnsupportedOperationException("工具类不能被实例化");
    }

    /**
     * 生成邀请码（使用默认配置）
     *
     * @param userId 用户ID，不能为null或空字符串
     * @param length 邀请码长度，范围 [{@value MIN_LENGTH}, {@value MAX_LENGTH}]
     * @return 生成的邀请码
     * @throws IllegalArgumentException      当参数无效时
     * @throws InviteCodeGenerationException 当生成过程出现异常时
     */
    public static String generateInviteCode(String userId, int length) {
        return generateInviteCode(userId, length, DEFAULT_PADDING_CHAR);
    }

    /**
     * 生成邀请码（使用默认长度）
     *
     * @param userId 用户ID，不能为null或空字符串
     * @return 生成的邀请码
     * @throws IllegalArgumentException      当参数无效时
     * @throws InviteCodeGenerationException 当生成过程出现异常时
     */
    public static String generateInviteCode(String userId) {
        return generateInviteCode(userId, DEFAULT_LENGTH, DEFAULT_PADDING_CHAR);
    }

    /**
     * 生成邀请码（完整参数版本）
     *
     * @param userId      用户ID，不能为null或空字符串
     * @param length      邀请码长度，范围 [{@value MIN_LENGTH}, {@value MAX_LENGTH}]
     * @param paddingChar 填充字符，必须是字符集中的有效字符
     * @return 生成的邀请码
     * @throws IllegalArgumentException      当参数无效时
     * @throws InviteCodeGenerationException 当生成过程出现异常时
     */
    private static String generateInviteCode(String userId, int length, char paddingChar) {
        // 参数验证
        validateParameters(userId, length, paddingChar);

        try {
            // 生成唯一输入
            String uniqueInput = createUniqueInput(userId);

            // 生成哈希值
            byte[] hash = generateHash(uniqueInput);

            // 转换为邀请码
            return convertToInviteCode(hash, length, paddingChar);

        } catch (NoSuchAlgorithmException e) {
            throw new InviteCodeGenerationException("哈希算法不可用: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new InviteCodeGenerationException("生成邀请码时发生异常: " + e.getMessage(), e);
        }
    }

    /**
     * 验证输入参数
     */
    private static void validateParameters(String userId, int length, char paddingChar) {
        Objects.requireNonNull(userId, "用户ID不能为null");

        if (userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空字符串");
        }

        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("邀请码长度必须在 %d 到 %d 之间", MIN_LENGTH, MAX_LENGTH));
        }

        if (CLEAR_UPPERCASE_CHARS.indexOf(paddingChar) == -1) {
            throw new IllegalArgumentException("填充字符必须是有效字符集中的字符: " + CLEAR_UPPERCASE_CHARS);
        }
    }

    /**
     * 创建唯一输入字符串
     */
    private static String createUniqueInput(String userId) {
        long nanoTime = System.nanoTime();
        int randomValue = SECURE_RANDOM.nextInt();
        long threadId = Thread.currentThread().threadId();

        return String.join("_",
                userId.trim(),
                String.valueOf(nanoTime),
                String.valueOf(randomValue),
                String.valueOf(threadId)
        );
    }

    /**
     * 生成SHA-256哈希值
     */
    private static byte[] generateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将哈希值转换为邀请码
     */
    private static String convertToInviteCode(byte[] hash, int length, char paddingChar) {
        long hashValue = Math.abs(bytesToLong(hash));

        StringBuilder codeBuilder = new StringBuilder(length);

        // 转换为指定进制
        while (hashValue > 0 && codeBuilder.length() < length) {
            codeBuilder.append(CLEAR_UPPERCASE_CHARS.charAt((int) (hashValue % BASE)));
            hashValue /= BASE;
        }

        // 处理长度
        return adjustLength(codeBuilder.toString(), length, paddingChar);
    }

    /**
     * 调整邀请码长度
     */
    private static String adjustLength(String code, int targetLength, char paddingChar) {
        if (code.length() == targetLength) {
            return code;
        }

        if (code.length() < targetLength) {
            // 填充到目标长度
            StringBuilder paddedCode = new StringBuilder(code);
            while (paddedCode.length() < targetLength) {
                paddedCode.append(paddingChar);
            }
            return paddedCode.toString();
        } else {
            // 截取到目标长度
            return code.substring(0, targetLength);
        }
    }

    /**
     * 将字节数组转换为长整型
     * 只取前8个字节避免溢出
     *
     * @param bytes 字节数组
     * @return 长整型值
     */
    private static long bytesToLong(byte[] bytes) {
        long result = 0;
        int bytesToProcess = Math.min(8, bytes.length);

        for (int i = 0; i < bytesToProcess; i++) {
            result = (result << 8) + (bytes[i] & 0xFF);
        }

        return result;
    }

    /**
     * 邀请码生成异常
     */
    public static class InviteCodeGenerationException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1L;

        public InviteCodeGenerationException(String message) {
            super(message);
        }

        public InviteCodeGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}