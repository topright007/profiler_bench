package com.profiler.cpu.util;

/**
 * Sink for computation results to prevent dead-code elimination.
 * Compatible with JMH Blackhole contract; use {@link JmhBlackholeAdapter} when running under JMH.
 */
public interface BlackholeSink {
    void consume(long value);
    void consume(boolean value);
    void consume(double value);
    void consume(Object value);
}
