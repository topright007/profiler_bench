package com.profiler.cpu.util;

import org.openjdk.jmh.infra.Blackhole;

/**
 * Adapts JMH Blackhole to {@link BlackholeSink} for use when running under JMH harness.
 */
public class JmhBlackholeAdapter implements BlackholeSink {
    private final Blackhole blackhole;

    public JmhBlackholeAdapter(Blackhole blackhole) {
        this.blackhole = blackhole;
    }

    @Override
    public void consume(long value) { blackhole.consume(value); }
    @Override
    public void consume(boolean value) { blackhole.consume(value); }
    @Override
    public void consume(double value) { blackhole.consume(value); }
    @Override
    public void consume(Object value) { blackhole.consume(value); }
}
