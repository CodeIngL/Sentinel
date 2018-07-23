package com.alibaba.csp.sentinel.property;

public interface SentinelProperty<T> {

    void addListener(PropertyListener<T> listener);

    void removeListener(PropertyListener<T> listener);

    void updateValue(T newValue);
}
