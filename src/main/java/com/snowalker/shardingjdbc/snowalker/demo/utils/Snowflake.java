package com.snowalker.shardingjdbc.snowalker.demo.utils;

public final class Snowflake {
    private static final long EPOCH = 1700000000000L;

    private static final long DATACENTER_BITS = 5L;
    private static final long WORKER_BITS = 5L;
    private static final long SEQ_BITS = 12L;

    private static final long MAX_DATACENTER = ~(-1L << DATACENTER_BITS); // 31
    private static final long MAX_WORKER = ~(-1L << WORKER_BITS);         // 31
    private static final long SEQ_MASK = ~(-1L << SEQ_BITS);              // 4095

    private static final long WORKER_SHIFT = SEQ_BITS;
    private static final long DATACENTER_SHIFT = SEQ_BITS + WORKER_BITS;
    private static final long TS_SHIFT = SEQ_BITS + WORKER_BITS + DATACENTER_BITS;

    private final long datacenterId;
    private final long workerId;

    private long lastTs = -1L;
    private long seq = 0L;

    public Snowflake(long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER)
            throw new IllegalArgumentException("datacenterId 0~" + MAX_DATACENTER);
        if (workerId < 0 || workerId > MAX_WORKER)
            throw new IllegalArgumentException("workerId 0~" + MAX_WORKER);
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long ts = System.currentTimeMillis();
        if (ts < lastTs) { // 时钟回拨：等待追平（实验环境够用）
            while (ts < lastTs) ts = System.currentTimeMillis();
        }

        if (ts == lastTs) {
            seq = (seq + 1) & SEQ_MASK;
            if (seq == 0) {
                while ((ts = System.currentTimeMillis()) <= lastTs) {}
            }
        } else {
            seq = 0;
        }

        lastTs = ts;

        return ((ts - EPOCH) << TS_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (workerId << WORKER_SHIFT)
                | seq;
    }
}