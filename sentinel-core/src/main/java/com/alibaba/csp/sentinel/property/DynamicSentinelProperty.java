package com.alibaba.csp.sentinel.property;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.csp.sentinel.log.RecordLog;

/**
 * SentinelProperty的默认实现
 *
 * @param <T>
 */
public class DynamicSentinelProperty<T> implements SentinelProperty<T> {

    protected Set<PropertyListener<T>> listeners = Collections.synchronizedSet(new HashSet<PropertyListener<T>>());
    private T value = null;

    public DynamicSentinelProperty() {
    }

    public DynamicSentinelProperty(T value) {
        super();
        this.value = value;
    }

    /**
     * 添加监听器，并触发其load事件
     *
     * @param listener
     */
    @Override
    public void addListener(PropertyListener<T> listener) {
        listeners.add(listener);
        listener.configLoad(value);
    }

    /**
     * 删除监听器
     *
     * @param listener
     */
    @Override
    public void removeListener(PropertyListener<T> listener) {
        listeners.remove(listener);
    }

    /**
     * 更新，委托给相应的监听器处理
     *
     * @param newValue
     */
    @Override
    public void updateValue(T newValue) {
        if (isEqual(value, newValue)) {
            return;
        }
        RecordLog.info("SentinelProperty, config is real updated to: " + newValue);

        value = newValue;
        for (PropertyListener<T> listener : listeners) {
            listener.configUpdate(newValue);
        }

    }

    /**
     * 新值旧值是否相等
     *
     * @param oldValue
     * @param newValue
     * @return
     */
    public boolean isEqual(T oldValue, T newValue) {
        if (oldValue == null && newValue == null) {
            return true;
        }

        if (oldValue == null && newValue != null) {
            return false;
        }

        return oldValue.equals(newValue);
    }

    /**
     * 关闭
     */
    public void close() {
        listeners.clear();
    }
}
