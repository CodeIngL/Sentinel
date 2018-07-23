package com.alibaba.csp.sentinel.property;

/**
 * A {@link SentinelProperty} that will never inform the {@link PropertyListener} on it.
 *
 * @author leyou
 */
public final class NoOpSentinelProperty implements SentinelProperty<Object> {
    @Override
    public void addListener(PropertyListener<Object> listener) { }

    @Override
    public void removeListener(PropertyListener<Object> listener) { }

    @Override
    public void updateValue(Object newValue) { }
}
