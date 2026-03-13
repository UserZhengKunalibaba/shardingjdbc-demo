package com.snowalker.shardingjdbc.snowalker.demo.utils;

public final class ShardKeyUtil {
    private ShardKeyUtil() {}

    // 从 userId 提取最后两位数字（00-99）
    public static int last2Digits(String userId) {
        if (userId == null) throw new IllegalArgumentException("userId is null");
        int n = userId.length();
        if (n < 2) throw new IllegalArgumentException("userId too short: " + userId);

        char c1 = userId.charAt(n - 2);
        char c2 = userId.charAt(n - 1);

        if (c1 < '0' || c1 > '9' || c2 < '0' || c2 > '9') {
            throw new IllegalArgumentException("userId last 2 chars not digits: " + userId);
        }
        return (c1 - '0') * 10 + (c2 - '0');
    }

    public static String twoDigits(int v) {
        // v 0..99
        return (v < 10) ? ("0" + v) : String.valueOf(v);
    }
}