package com.alibaba.csp.sentinel.property;

/**
 * 属性器。会绑定监听该属性器的监听器。触发监听器相应的事件
 * @param <T>
 */
public interface SentinelProperty<T> {

    /**
     * 为属性器绑定事件，触发监听器事件
     * @param listener
     */
    void addListener(PropertyListener<T> listener);

    /**
     * 删除监听器
     * @param listener
     */
    void removeListener(PropertyListener<T> listener);

    /**
     * 更新值，触发监听器事件
     * @param newValue
     */
    void updateValue(T newValue);
}
