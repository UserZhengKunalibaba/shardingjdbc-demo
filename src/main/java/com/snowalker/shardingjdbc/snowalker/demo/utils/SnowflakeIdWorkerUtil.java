package com.snowalker.shardingjdbc.snowalker.demo.utils;

public class SnowflakeIdWorkerUtil {
    // ========== 可调参数 ==========
    // 你可以按需改起始纪元（减少生成值的长度）
    private static final long EPOCH = 1700000000000L; // 2023-11-14 22:13:20 UTC 左右

    // 位宽分配（经典雪花）
    private static final long WORKER_ID_BITS = 10L;      // 0~1023
    private static final long SEQUENCE_BITS  = 12L;      // 0~4095

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // ========== 实例字段 ==========
    private final long workerId;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdWorkerUtil(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("workerId must be between 0 and " + MAX_WORKER_ID);
        }
        this.workerId = workerId;
    }

    /** 返回纯数字 long（天然全是数字，最后两位当然也是数字） */
    public synchronized long nextId() {
        long timestamp = currentTime();

        // 时钟回拨处理：等待到 lastTimestamp
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            // 小回拨直接等，大回拨你可以改成抛异常
            timestamp = waitUntil(lastTimestamp, offset);
        }

        if (timestamp == lastTimestamp) {
            // 同毫秒内 sequence++
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // sequence 溢出：等到下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒：重置 sequence（也可以用随机起步，分布更均匀）
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 返回 String，强制最后两位是数字 00~99（适合你用 substring 截取倒数两位分库分表）
     *
     * 注意：基础雪花 ID 已经唯一；尾部两位只是“可控分片后缀”，不用于唯一性也没问题。
     */
    public String nextIdWith2DigitsSuffix() {
        long id = nextId();
        int suffix = (int) (id % 100);
        // 你也可以用随机后缀（更均匀）：
        // int suffix = ThreadLocalRandom.current().nextInt(100);

        return Long.toString(id) + String.format("%02d", suffix);
    }

    // ========== 时间相关 ==========
    private long currentTime() {
        return System.currentTimeMillis();
    }

    private long tilNextMillis(long lastTs) {
        long ts = currentTime();
        while (ts <= lastTs) {
            ts = currentTime();
        }
        return ts;
    }

    private long waitUntil(long target, long offset) {
        // 简单等待：offset 小就等一下
        // 这里不 sleep 很久，避免卡死；你也可以按需调整
        long ts = currentTime();
        while (ts < target) {
            // 小忙等；如果你想更省 CPU，可以 Thread.sleep(1)
            ts = currentTime();
        }
        return ts;
    }
}