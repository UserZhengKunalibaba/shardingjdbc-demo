package com.snowalker.shardingjdbc.snowalker.demo.utils;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;

@Component
public class IdGen {
    private final Snowflake snowflake;

    public IdGen() {
        long datacenterId = readEnvOrDefault("DC_ID", 0, 0, 31);
        long workerId = readEnvOrDefault("WORKER_ID", autoWorkerId(), 0, 31);
        this.snowflake = new Snowflake(datacenterId, workerId);

        System.out.println("[IdGen] datacenterId=" + datacenterId + ", workerId=" + workerId);
    }

    public long nextLong() {
        return snowflake.nextId();
    }

    /** 你要“后两位必须是数字”，直接用字符串即可 */
    public String nextWith2DigitsSuffix() {
        long id = snowflake.nextId();
        int suffix = (int) (Math.floorMod(id, 100)); // 00~99
        return Long.toString(id) + String.format("%02d", suffix);
    }

    private static long autoWorkerId() {
        try {
            String host = InetAddress.getLocalHost().getHostName();
            String pid = ManagementFactory.getRuntimeMXBean().getName(); // "pid@host"
            int h = (host + "|" + pid).hashCode();
            return (h & 0x7fffffff) % 32;
        } catch (Exception e) {
            // 兜底：随机一个（不建议生产，只给你本地跑通用）
            return (System.nanoTime() & 0x7fffffff) % 32;
        }
    }

    private static long readEnvOrDefault(String key, long def, long min, long max) {
        String v = System.getenv(key);
        if (v == null || v.trim().isEmpty()) return def;
        long x = Long.parseLong(v.trim());
        if (x < min || x > max) throw new IllegalArgumentException(key + " out of range " + min + "~" + max);
        return x;
    }
}