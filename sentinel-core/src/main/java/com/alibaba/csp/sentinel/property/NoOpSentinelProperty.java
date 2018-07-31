package com.alibaba.csp.sentinel.property;

/**
 * A {@link SentinelProperty} that will never inform the {@link PropertyListener} on it.
 * <p>
 * <p>
 * {@link SentinelProperty}永远不会通知{@link PropertyListener} 。
 * </p>
 *
 * @author leyou
 */
public final class NoOpSentinelProperty implements SentinelProperty<Object> {
    @Override
    public void addListener(PropertyListener<Object> listener) {
        //nothing
    }

    @Override
    public void removeListener(PropertyListener<Object> listener) {
        //nothing
    }

    @Override
    public void updateValue(Object newValue) {
        //nothing
    }
}
