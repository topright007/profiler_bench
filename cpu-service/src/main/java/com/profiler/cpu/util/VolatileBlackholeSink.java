package com.profiler.cpu.util;

import org.springframework.stereotype.Component;

/**
 * Volatile-based sink to prevent JIT from eliminating dead code.
 * Use when not running under JMH; under JMH use {@link JmhBlackholeAdapter} with injected Blackhole.
 */
@Component
public class VolatileBlackholeSink implements BlackholeSink {
    private volatile long l;
    private volatile boolean z;
    private volatile double d;
    private volatile Object o;

    @Override
    public void consume(long value) { l = value; }
    @Override
    public void consume(boolean value) { z = value; }
    @Override
    public void consume(double value) { d = value; }
    @Override
    public void consume(Object value) { o = value; }
}
